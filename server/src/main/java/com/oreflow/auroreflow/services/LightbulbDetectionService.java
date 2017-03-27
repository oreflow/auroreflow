package com.oreflow.auroreflow.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.util.Lightbulbs;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class LightbulbDetectionService {
  private static final Logger logger = Logger.getLogger(LightbulbDetectionService.class.getName());

  private static final Duration POLLING_INTERVAL = Duration.ofSeconds(30);
  private static final int BROADCAST_PORT = 1982;
  private static final String BROADCAST_ADDRESS = "239.255.255.250";
  private static final byte [] BROADCAST_MESSAGE = (""
      + "M-SEARCH * HTTP/1.1\r\n"
      + "HOST: 239.255.255.250:1982\r\n"
      + "MAN: \"ssdp:discover\"\r\n"
      + "ST: wifi_bulb").getBytes();

  private final MulticastSocket socket;
  private final DatagramPacket broadcastPacket;
  private final LightbulbService lightbulbService;

  private Instant lastPollInstant;

  @Inject
  public LightbulbDetectionService(LightbulbService lightbulbService) throws IOException {
    this.lightbulbService = lightbulbService;
    lastPollInstant = Instant.EPOCH;
    socket = new MulticastSocket(BROADCAST_PORT);
    InetAddress inetAddress = InetAddress.getByName(BROADCAST_ADDRESS);
    broadcastPacket = new DatagramPacket(BROADCAST_MESSAGE, BROADCAST_MESSAGE.length, inetAddress, BROADCAST_PORT);
    socket.joinGroup(inetAddress);

    Thread broadcastListener = new Thread(this::broadcastListenerImpl);
    broadcastListener.start();
    broadcastForLightbulbs();

    Thread statusPollingThread = new Thread(this::statusPollingThreadImpl);
    statusPollingThread.start();
  }

  /**
   * Endless loop that polls for status every {@literal POLLING_INTERVAL}
   */
  private void statusPollingThreadImpl() {
    while (true) {
      try {
        Thread.sleep(POLLING_INTERVAL.toMillis());
        pollStatus();
        lastPollInstant = Instant.now();
      } catch (InterruptedException e) {
        logger.log(Level.SEVERE, "Bulb status polling thread got interrupted");
        return;
      }
    }
  }

  /**
   * Polls connection status on all active {@link Lightbulb}s that have not been accessed since the last poll.
   */
  private void pollStatus() {
    Collection<Lightbulb> activeLightbulbs = lightbulbService.getActiveLightbulbs();
    if(activeLightbulbs.isEmpty()) {
      broadcastForLightbulbs();
    }
    for (Lightbulb lightbulb : activeLightbulbs) {
      Instant lastRequestInstant = lightbulbService.getLastRequestInstantOrDefault(lightbulb.getId());
      if (lastRequestInstant.isBefore(lastPollInstant)) {
          lightbulbService.sendLightbulbRequest(lightbulb.getId(), LightbulbRequest.getDefaultInstance());
      }
    }
  }

  /**
   * Infinite loop that listens to broadcast messages
   */
  private void broadcastListenerImpl() {
    logger.log(Level.INFO, "Starting Broadcast listener");
    while (true) {
      byte[] recvBuf = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
      try {
        socket.receive(receivePacket);
        if(Lightbulbs.isBulbAdvertisement(recvBuf)) {
          lightbulbService.putLightbulb(Lightbulbs.parseAdvertisement(recvBuf));
        }
      } catch (IOException e) {
        logger.log(Level.SEVERE, "BroadcastListener crashed with \n" + e.getMessage()
            + "\n sleeping 15 seconds before attempting to listen again");
        try {
          Thread.sleep(Duration.ofSeconds(15).toMillis());
        } catch (InterruptedException e1) {
          logger.log(Level.SEVERE, "Bulb status polling thread got interrupted");
        }
      }
    }
  }

  /**
   * Sends a broadcast message to request all active lightbulbs on the network to advertise themselves
   */
  private void broadcastForLightbulbs(){
    try {
      logger.log(Level.INFO, "Sending broadcast message to find active lightbulbs to all network interfaces");
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface networkInterface = interfaces.nextElement();
        if (!networkInterface.getInetAddresses().hasMoreElements()) {
          continue;
        }
        socket.setNetworkInterface(networkInterface);
        socket.send(broadcastPacket);
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, String.format("Failed to broadcast for lightbulbs with exception\n%s"), e.getMessage());
    }
  }
}
