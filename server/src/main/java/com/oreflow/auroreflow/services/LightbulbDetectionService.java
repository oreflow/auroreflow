/**
 * Copyright 2017 Tim Malmström
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oreflow.auroreflow.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.util.LightbulbMessages;
import com.oreflow.auroreflow.util.Lightbulbs;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service managing broadcasts and listening for announcements of lightbulbs coming active on the network
 */
@Singleton
public class LightbulbDetectionService {
  private static final Logger logger = Logger.getLogger(LightbulbDetectionService.class.getName());

  private static final Duration POLLING_INTERVAL = Duration.ofSeconds(30);
  private static final Duration ALLOWED_DELAY = Duration.ofSeconds(5);

  private static final int BROADCAST_PORT = 1982;
  private static final String BROADCAST_ADDRESS = "239.255.255.250";
  private static final byte [] BROADCAST_MESSAGE = (""
      + "M-SEARCH * HTTP/1.1\r\n"
      + "HOST: 239.255.255.250:1982\r\n"
      + "MAN: \"ssdp:discover\"\r\n"
      + "ST: wifi_bulb").getBytes();

  private final MulticastSocket socket;
  private final DatagramPacket broadcastPacket;
  private final LightbulbSocketService lightbulbSocketService;
  private final LightbulbService lightbulbService;

  private Instant lastPollInstant;

  @Inject
  public LightbulbDetectionService(LightbulbSocketService lightbulbSocketService,
                                   LightbulbService lightbulbService) throws IOException {
    this.lightbulbSocketService = lightbulbSocketService;
    this.lightbulbService = lightbulbService;
    lastPollInstant = Instant.EPOCH;
    socket = new MulticastSocket(BROADCAST_PORT);
    InetAddress inetAddress = InetAddress.getByName(BROADCAST_ADDRESS);
    broadcastPacket = new DatagramPacket(BROADCAST_MESSAGE, BROADCAST_MESSAGE.length,
        inetAddress, BROADCAST_PORT);
    socket.joinGroup(inetAddress);

    Thread broadcastListener = new Thread(this::broadcastListenerImpl);
    broadcastListener.start();
    broadcastForLightbulbs();

    Thread statusPollingThread = new Thread(this::statusPollingThreadImpl);
    statusPollingThread.start();
  }

  /**
   * Continous thread triggering polling every {@literal POLLING_INTERVAL}
   */
  private void statusPollingThreadImpl() {
    while (true) {
      try {
        Thread.sleep(POLLING_INTERVAL.minus(ALLOWED_DELAY).toMillis());
        pollStatus();
        lastPollInstant = Instant.now();
        Thread.sleep(ALLOWED_DELAY.toMillis());
        updateActiveStatus();
      } catch (InterruptedException e) {
        logger.log(Level.SEVERE, "Bulb status polling thread got interrupted");
        return;
      }
    }
  }

  /**
   * Polls current power status on all active {@link Lightbulb}s that have not been
   * accessed since the last poll. Used to detect lightbulbs that have gone offline.
   */
  private void pollStatus() {
    Collection<Lightbulb> activeLightbulbs = lightbulbService.getAllLightbulbs();
    if(activeLightbulbs.isEmpty()) {
      broadcastForLightbulbs();
    }
    for (Lightbulb lightbulb : activeLightbulbs) {
      Instant lastRequestInstant =
          lightbulbSocketService.getLastSentRequestInstantOrEpoch(lightbulb.getId());
      if (lastRequestInstant.isBefore(lastPollInstant)) {
          lightbulbSocketService.sendLightbulbRequest(lightbulb,
              LightbulbRequest.getDefaultInstance());
      }
    }
  }

  /**
   * Checks all {@link Lightbulb}s whether they have returned a response since the last poll
   * and marks them as active/inactive accordingly.
   */
  private void updateActiveStatus() {
    Collection<Lightbulb> lightbulbs = lightbulbService.getAllLightbulbs();
    for (Lightbulb lightbulb : lightbulbs) {
      Instant lastRequest =
          lightbulbSocketService.getLastSentRequestInstantOrEpoch(lightbulb.getId());
      Instant lastResponse =
          lightbulbSocketService.getLastResponseInstantOrEpoch(lightbulb.getId());
      if(lastResponse.isBefore(lastRequest)) {
        lightbulbService.updateLightbulb(lightbulb.toBuilder().setIsActive(false).build());
      } else {
        lightbulbService.updateLightbulb(lightbulb.toBuilder().setIsActive(true).build());
      }
    }
  }

  /**
   * Continous listener for lightbulbs announcing themselves on the network
   */
  private void broadcastListenerImpl() {
    logger.log(Level.INFO, "Starting Broadcast listener");
    while (true) {
      byte[] recvBuf = new byte[2048];
      DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
      try {
        socket.receive(receivePacket);
        if (Lightbulbs.isBulbAdvertisement(recvBuf)) {
          Lightbulb lightbulb = Lightbulbs.parseAdvertisement(recvBuf);
          if (lightbulbService.hasLightbulb(lightbulb.getId())) {
            Lightbulb existingLightbulb = lightbulbService.getLightbulb(lightbulb.getId());
            lightbulbSocketService.sendLightbulbRequest(existingLightbulb,
                LightbulbMessages.createRestoreRequest(existingLightbulb));
          } else {
            lightbulbService.putLightbulb(lightbulb);
          }
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
   * Sends a broadcast message to detect all active {@link Lightbulb}s on the network.
   */
  private void broadcastForLightbulbs(){
    try {
      logger.log(Level.INFO,
          "Sending broadcast message to find active lightbulbs to all network interfaces");
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
      logger.log(Level.SEVERE,
          String.format("Failed to broadcast for lightbulbs with exception\n%s"), e.getMessage());
    }
  }
}
