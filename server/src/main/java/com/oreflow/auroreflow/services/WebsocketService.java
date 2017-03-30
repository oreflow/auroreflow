package com.oreflow.auroreflow.services;

import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.util.JsonUtil;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class WebsocketService {
  private final Set<Session> activeSessions;

  WebsocketService() {
    this.activeSessions = new HashSet<>();
  }

  public void addSocketSession(Session session) {
    activeSessions.add(session);
  }

  void broadcastLightbulbUpdate(Lightbulb lightbulb) {
    Set<Session> closedSessions = new HashSet<>();
    for (Session session : activeSessions) {
      if (session.isOpen()) {
        session.getAsyncRemote().sendText(JsonUtil.toJSON(lightbulb));
      } else {
        closedSessions.add(session);
      }
    }
    activeSessions.removeAll(closedSessions);
  }
}
