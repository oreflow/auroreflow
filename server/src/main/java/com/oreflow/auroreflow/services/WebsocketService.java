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

import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.util.JsonUtil;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to manage WebSockets and client connections
 */
@Singleton
public class WebsocketService {
  private final Set<Session> activeSessions;

  WebsocketService() {
    this.activeSessions = new HashSet<>();
  }

  public void addSocketSession(Session session) {
    activeSessions.add(session);
  }

  /**
   * Broadcasts that a given {@link Lightbulb} has been updated to all connected clients
   */
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
