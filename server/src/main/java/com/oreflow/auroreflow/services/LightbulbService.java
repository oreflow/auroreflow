package com.oreflow.auroreflow.services;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbCommandResponse;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.util.JsonUtil;
import com.oreflow.auroreflow.util.LightbulbMessages;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Service to manage
 */
@Singleton
public class LightbulbService {
  private static final Logger logger = Logger.getLogger(LightbulbService.class.getName());
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(1);
  private static final Duration MIN_DELAY_BETWEEN_REQUESTS = Duration.ofMillis(300);

  private final Map<Long, Lightbulb> lightbulbs;
  private final Map<Long, Long> lastRequestId;
  private final Map<Long, Instant> lastRequestTimes;
  private final Map<Long, Socket> sockets;

  /** Maps containing threads to throttle requests*/
  private final Map<Long, LightbulbRequest> pendingRequest;
  private final Map<Long, Thread> pushbackThread;

  LightbulbService() {
    lightbulbs = new ConcurrentHashMap<>();
    lastRequestId = new ConcurrentHashMap<>();
    lastRequestTimes = new ConcurrentHashMap<>();
    sockets = new ConcurrentHashMap<>();
    pendingRequest = new ConcurrentHashMap<>();
    pushbackThread = new ConcurrentHashMap<>();
  }

  /**
   * Adds a new {@link Lightbulb} and disposes potential old socket for same id
   */
  void putLightbulb(Lightbulb lightbulb) throws IOException {
    logger.log(Level.INFO, "Adding new / replacing Lightbulb with \n" + lightbulb);
    final long lightbulbId = lightbulb.getId();
    lightbulbs.put(lightbulbId, lightbulb);

    if (sockets.containsKey(lightbulbId)) {
      Socket currentSocket = sockets.get(lightbulbId);
      if (!currentSocket.isClosed()) {
        currentSocket.close();
      }
      sockets.remove(lightbulbId);
    }
  }

  /**
   * Gets or creates a socket for a given lightbulb
   */
  private Socket getSocketForLightbulb(Lightbulb lightbulb) throws IOException {
    if (sockets.containsKey(lightbulb.getId())) {
      Socket currentSocket = sockets.get(lightbulb.getId());
      if (currentSocket.isConnected() && !currentSocket.isClosed()) {
        return currentSocket;
      }
    }
    Socket newSocket = new Socket(lightbulb.getIp(), lightbulb.getPort());
    newSocket.setSoTimeout((int) REQUEST_TIMEOUT.toMillis());
    sockets.put(lightbulb.getId(), newSocket);
    return newSocket;
  }

  /**
   * Sends a given {@link LightbulbRequest} to a given lightbulb
   */
  public void sendLightbulbRequest(long lightbulbId, LightbulbRequest lightbulbRequest) {
    if (requestNeedsToBeThrottled(lightbulbId)) {
      logger.log(Level.INFO, "Request got throttled", lightbulbRequest);
      enqueueThrottledRequest(lightbulbId, lightbulbRequest);
      return;
    }
    final Lightbulb lightbulb = getLightbulb(lightbulbId);
    final long requestId = lastRequestId.getOrDefault(lightbulbId, 0L) + 1;
    lastRequestId.put(lightbulbId, requestId);
    sendMessage(lightbulb, requestId, lightbulbRequest);
  }

  /**
   * Determines if a request right now to a given lightbulbId needs to be throttled
   */
  private boolean requestNeedsToBeThrottled(long lightbulbId) {
    final Instant lastRequestTime = lastRequestTimes.getOrDefault(lightbulbId, Instant.EPOCH);
    final Instant nextAllowedTime = lastRequestTime.plus(MIN_DELAY_BETWEEN_REQUESTS);
    return nextAllowedTime.isAfter(Instant.now());
  }

  /**
   * Ensures there is a thread sending the latest pending {@link LightbulbRequest} as soon as the request throttling
   * allows it
   */
  private void enqueueThrottledRequest(final long lightbulbId, final LightbulbRequest lightbulbRequest) {
    final Instant lastRequestTime = lastRequestTimes.getOrDefault(lightbulbId, Instant.EPOCH);
    final Instant nextAllowedTime = lastRequestTime.plus(MIN_DELAY_BETWEEN_REQUESTS);
    final Duration waitDuration = Duration.between(Instant.now(), nextAllowedTime).plus(Duration.ofMillis(5));

    if (!pushbackThread.containsKey(lightbulbId)) {
      Thread pushback = new Thread(() -> {
        try {
          Thread.sleep(Math.max(waitDuration.toMillis(), 0));
          this.sendLightbulbRequest(lightbulbId, pendingRequest.get(lightbulbId));
        } catch (InterruptedException e) {
          logger.log(Level.SEVERE, "Got interrupted while waiting for next allowed request time");
        }
        pushbackThread.remove(lightbulbId);
        pendingRequest.remove(lightbulbId);
      });
      pushbackThread.put(lightbulbId, pushback);
      pushback.start();
    }
    pendingRequest.put(lightbulbId, lightbulbRequest);
  }

  /**
   * Sends a given {@link LightbulbRequest} to a given {@link Lightbulb}
   */
  private void sendMessage(final Lightbulb lightbulb, final long requestId, final LightbulbRequest lightbulbRequest) {
    try {
      final String lightbulbRequestMessage = LightbulbMessages.createMessage(requestId, lightbulbRequest);
      final Socket socket = getSocketForLightbulb(lightbulb);
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      logger.log(Level.INFO,
          String.format("Sending message to \nip %s, port %d\nMessage\n%s\n", lightbulb.getIp(), lightbulb.getPort(),
              lightbulbRequestMessage));
      out.print(lightbulbRequestMessage);
      out.flush();
      lastRequestTimes.put(lightbulb.getId(), Instant.now());
      new Thread(() -> {
        try {
          String responseMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()))
              .lines()
              .collect(Collectors.joining());
          LightbulbCommandResponse response = JsonUtil.parseCommandResponse(responseMessage);
          if (response.getId() == requestId && !response.getResultList().isEmpty()) {
            logger.log(Level.INFO, "SUCCESS" + response);
            updateLightbulbWith(lightbulb, lightbulbRequest);

          } else {
            logger.log(Level.SEVERE,
                String.format("Got response \n%s\n for another request or non successful response\n" +
                    " Disregarding results", response));
          }
        } catch (SocketTimeoutException e) {
          logger.log(Level.WARNING,
              String.format("Got no response for lightbulb %d within given deadline.\n" +
                  "Marking lightbulb as inactive", lightbulb.getId()));
          updateLightbulb(lightbulb.toBuilder().setIsActive(false).build());
        } catch (IOException e) {
          logger.log(Level.SEVERE, "Error when trying to read response from lightbulb" + e.getMessage());
        }
      }).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the last Request {@link Instant} or default EPOCH if no previous request has been registered
   */
  public Instant getLastRequestInstantOrDefault(long lightbulbId) {
    return lastRequestTimes.getOrDefault(lightbulbId, Instant.EPOCH);
  }

  /**
   * Gets a {@link Lightbulb} by Id, or throws exception if it does not exist
   */
  public Lightbulb getLightbulb(long id) {
    if (lightbulbs.containsKey(id)) {
      return lightbulbs.get(id);
    }
    throw new IllegalArgumentException("Lightbulb ID does not exist " + id);
  }

  /**
   *  Updates the stored {@link Lightbulb} with the information in given {@link LightbulbRequest}
   */
  public void updateLightbulbWith(Lightbulb lightbulb, LightbulbRequest lightbulbRequest) {
    switch (lightbulbRequest.getRequestTypeCase()) {
      case HSV_REQUEST:
        updateLightbulb(
            lightbulb.toBuilder()
                .setHue(lightbulbRequest.getHsvRequest().getHue())
                .setSat(lightbulbRequest.getHsvRequest().getSat())
                .setBright(lightbulbRequest.getHsvRequest().getBrightness())
                .setColorMode(Lightbulb.ColorMode.COLOR_MODE)
                .build());
        break;
      case CT_REQUEST:
        updateLightbulb(
            lightbulb.toBuilder()
                .setHue(lightbulbRequest.getCtRequest().getCt())
                .setBright(lightbulbRequest.getHsvRequest().getBrightness())
                .setColorMode(Lightbulb.ColorMode.COLOR_TEMPERATURE_MODE)
                .build());
        break;
      case POWER_REQUEST:
        updateLightbulb(
            lightbulb.toBuilder()
                .setPower(lightbulbRequest.getPowerRequest().getPower())
                .build());
        break;
      case REQUESTTYPE_NOT_SET:
        updateLightbulb(lightbulb.toBuilder().setIsActive(true).build());
        break;
    }
    lightbulbs.put(lightbulb.getId(), lightbulb);
  }

  /**
   * Updates stored {@link Lightbulb}
   */
  public void updateLightbulb(Lightbulb lightbulb) {
    lightbulbs.put(lightbulb.getId(), lightbulb);
  }

  /**
   * Gets a list of all {@link Lightbulb}s where is_active = true
   */
  public ImmutableCollection<Lightbulb> getActiveLightbulbs() {
    return lightbulbs.values().stream().filter(Lightbulb::getIsActive).collect(ImmutableList.toImmutableList());
  }

  /**
   * Gets a list of all {@link Lightbulb}s
   */
  public ImmutableCollection<Lightbulb> getAllLightbulbs() {
    return ImmutableList.copyOf(lightbulbs.values());
  }
}
