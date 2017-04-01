package com.oreflow.auroreflow.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.util.JsonUtil;
import com.oreflow.auroreflow.util.LightbulbMessages;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Service to manage connections and messaging with lightbulbs
 */
@Singleton
public class LightbulbSocketService {

  private class ImmutableRequest {
    Long requestId;
    Instant requestCreatedTime;
    Lightbulb lightbulb;
    LightbulbRequest lightbulbRequest;
    ImmutableRequest(Lightbulb lightbulb, LightbulbRequest lightbulbRequest) {
      this.requestId = lastRequestId.getOrDefault(lightbulb.getId(), 1L) + 1;
      lastRequestId.put(lightbulb.getId(), requestId);
      this.requestCreatedTime = Instant.now();
      this.lightbulb = lightbulb;
      this.lightbulbRequest = lightbulbRequest;
    }

    Long getRequestId() {
      return requestId;
    }

    LightbulbRequest getLightbulbRequest() {
      return lightbulbRequest;
    }

    Lightbulb getLightbulb() {
      return lightbulb;
    }

    Long getLightbulbId() {
      return lightbulb.getId();
    }
  }

  private static final Logger logger = Logger.getLogger(LightbulbSocketService.class.getName());
  private static final Duration MIN_DELAY_BETWEEN_REQUESTS = Duration.ofMillis(300);


  private final Map<Long, LinkedList<ImmutableRequest>> requests;
  private final Map<Long, Socket> sockets;
  private final Map<Long, Thread> socketReaders;
  private final Map<Long, Instant> lastSentRequestInstant;
  private final Map<Long, Instant> lastResponseInstant;
  private final Map<Long, Long> lastRequestId;

  /** Maps containing threads to throttle requests*/
  private final Map<Long, Thread> pushbackThread;
  private final LightbulbService lightbulbService;

  @Inject
  LightbulbSocketService(LightbulbService lightbulbService) {
    this.lightbulbService = lightbulbService;
    sockets = new ConcurrentHashMap<>();
    requests = new ConcurrentHashMap<>();
    pushbackThread = new ConcurrentHashMap<>();
    lastSentRequestInstant = new ConcurrentHashMap<>();
    lastResponseInstant = new ConcurrentHashMap<>();
    socketReaders = new ConcurrentHashMap<>();
    lastRequestId = new ConcurrentHashMap<>();
  }

  /** Sends the fiven request to the lightbulb with given lightbulbId */
  public void sendLightbulbRequest(Long lightbulbId, LightbulbRequest lightbulbRequest) {
    sendLightbulbRequest(lightbulbService.getLightbulb(lightbulbId), lightbulbRequest);
  }


  /** Handles throttling for requests and sends a given request to the given lightbulb */
  public void sendLightbulbRequest(Lightbulb lightbulb, LightbulbRequest lightbulbRequest) {
    Instant nextAllowedSendTime = lastSentRequestInstant.getOrDefault(lightbulb.getId(), Instant.EPOCH)
        .plus(MIN_DELAY_BETWEEN_REQUESTS);
    ImmutableRequest request = new ImmutableRequest(lightbulb, lightbulbRequest);
    addRequestLast(request);
    if(nextAllowedSendTime.isAfter(Instant.now())) {
      launchThrottlingThread(request.getLightbulbId(), nextAllowedSendTime);
      return;
    }
    try {
      sendRequest(request);
    } catch (IOException e) {
      logger.log(Level.SEVERE,
          String.format("Lightbulb request failed with error %s: %s", e.getClass(), e.getMessage()));
      handleError(request);
    }
  }

  /**
   * Sends a {@link ImmutableRequest to its contained lightbulb}
   */
  private void sendRequest(final ImmutableRequest request) throws IOException {
    lastSentRequestInstant.put(request.getLightbulbId(), Instant.now());
    final String requestJsonString =
        LightbulbMessages.createMessage(request.getRequestId(), request.getLightbulbRequest());
    Socket socket = getSocketForLightbulb(request.getLightbulb());
    new Thread(() ->{
      try {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.print(requestJsonString);
        out.flush();
      } catch (IOException e) {
        handleError(request);
      }
    }).start();
  }

  /**
   * Gets or creates a new socket for a given lightbulb
   */
  private Socket getSocketForLightbulb(Lightbulb lightbulb) throws IOException {
    if (lastResponseInstant.containsKey(lightbulb.getId())
        && lastSentRequestInstant.containsKey(lightbulb.getId())
        && lastResponseInstant.get(lightbulb.getId()).plus(Duration.ofSeconds(20))
          .isBefore(lastSentRequestInstant.get(lightbulb.getId()))) {
      disposeSocket(lightbulb);
    }
    if(sockets.containsKey(lightbulb.getId())) {
      Socket socket = sockets.get(lightbulb.getId());
      if(socket.isConnected() && !socket.isClosed()) {
        return socket;
      }
      disposeSocket(lightbulb);
    }
    return createNewSocket(lightbulb);
  }


  /**
   * Creates a new socket for a given lightbulb and starts up a separate thread that
   * continously reads from the created socket
   */
  private Socket createNewSocket(final Lightbulb lightbulb) throws IOException {
    final Socket socket = new Socket(lightbulb.getIp(), lightbulb.getPort());
    Thread socketReader = new Thread(() -> {
      try {
        Reader reader = new InputStreamReader(socket.getInputStream());
        char[] messageBuffer = new char[1024];
        while(socket.isConnected() && !Thread.interrupted()) {
          reader.read(messageBuffer);
          handleResponse(lightbulb, new String(messageBuffer));
          Thread.sleep(10);
        }
      } catch (SocketException e) {
        logger.log(Level.INFO, String.format("Socket for %d closed.", lightbulb.getId()));
      }
      catch (InterruptedException|IOException e) {
        e.printStackTrace();
        logger.log(Level.SEVERE, String.format("Socket reader for lightbulb %s terminated unexpectedly", lightbulb));
      }
    });
    System.out.println("Launching new reader thread");
    socketReaders.put(lightbulb.getId(), socketReader);
    socketReader.start();
    sockets.put(lightbulb.getId(), socket);
    return socket;
  }

  /** Disposes of an existing socket and its read-thread */
  private void disposeSocket(Lightbulb lightbulb) throws IOException {
    Socket socket = sockets.get(lightbulb.getId());
    Thread reader = socketReaders.get(lightbulb.getId());
    if(reader != null) {
      reader.interrupt();
      socketReaders.remove(lightbulb.getId());
    }
    if(socket != null) {
      socket.close();
      sockets.remove(lightbulb.getId());
    }
  }

  /** Handles response messages from lightbulbs */
  private void handleResponse(Lightbulb lightbulb, String message) {
    lastResponseInstant.put(lightbulb.getId(), Instant.now());
    Arrays.stream(message.split("\r\n"))
        .filter(JsonUtil::isCommandResponse)
        .forEach(split -> {
          long requestId = JsonUtil.parseCommandResponseId(split);
          Optional<ImmutableRequest> request = getAndClearBefore(lightbulb.getId(), requestId);
          request.ifPresent(immutableRequest ->
              lightbulbService.updateLightbulbWith(
                  immutableRequest.getLightbulb(), immutableRequest.getLightbulbRequest()));
        });
    logger.log(Level.INFO,
        String.format("Received response message for lightbulb ID: %d\n%s\n", lightbulb.getId(), message));
  }

  private void handleError(ImmutableRequest request) {
    lightbulbService.updateLightbulb(request.getLightbulb().toBuilder().setIsActive(false).build());
  }

  /**
   * Launches a throttling thread for the given lightbulbId which will execute the last request as soon as the
   * throttling limit is out
   */
  private void launchThrottlingThread(final long lightbulbId, Instant nextAllowedTime) {
    final Duration waitDuration = Duration.between(Instant.now(), nextAllowedTime).plus(Duration.ofMillis(5));
    if (!pushbackThread.containsKey(lightbulbId)) {
      Thread pushback = new Thread(() -> {
        try {
          System.out.println("Sleeping" + waitDuration.toMillis());
          Thread.sleep(Math.max(waitDuration.toMillis(), 0));
          this.sendRequest(getLastRequest(lightbulbId));
        } catch (InterruptedException e) {
          logger.log(Level.SEVERE, "Got interrupted while waiting for next allowed lightbulbRequest time");
        } catch (IOException e) {
          e.printStackTrace();
        }
        pushbackThread.remove(lightbulbId);
      });
      pushbackThread.put(lightbulbId, pushback);
      pushback.start();
    }
  }

  Instant getLastSentRequestInstantOrEpoch(Long lightbulbId) {
    return lastSentRequestInstant.getOrDefault(lightbulbId, Instant.EPOCH);
  }

  Instant getLastResponseInstantOrEpoch(Long lightbulbId) {
    return lastResponseInstant.getOrDefault(lightbulbId, Instant.EPOCH);
  }

  /** Gets the last request for given lightbulbId */
  private ImmutableRequest getLastRequest(long lightbulbId) {
    return requests.get(lightbulbId).getLast();
  }

  /** Gets a specific requestId and removes all listed requests up until that id*/
  private Optional<ImmutableRequest> getAndClearBefore(long lightbulbId, long requestId) {
    LinkedList<ImmutableRequest> requestList = requests.get(lightbulbId);
    Optional<ImmutableRequest> request = Optional.empty();
    while(!requestList.isEmpty() && requestList.getFirst().getRequestId() <= requestId) {
      request = Optional.of(requestList.removeFirst());
    }
    if(request.isPresent() && request.get().getRequestId() != requestId) {
      return Optional.empty();
    }
    return request;
  }

  /** Adds a request to the back of its lightbulbs requestlist */
  private void addRequestLast(ImmutableRequest request) {
    if(!requests.containsKey(request.getLightbulbId())) {
      requests.put(request.getLightbulbId(), new LinkedList<>());
    }
    requests.get(request.getLightbulbId()).addLast(request);
  }
}
