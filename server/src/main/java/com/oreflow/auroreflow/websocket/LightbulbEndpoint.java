package com.oreflow.auroreflow.websocket;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.services.WebsocketService;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;


@Singleton
public class LightbulbEndpoint extends Endpoint {

  private final WebsocketService websocketService;

  @Inject
  public LightbulbEndpoint(WebsocketService websocketService) {
    this.websocketService = websocketService;
  }

  @Override
  public void onOpen(final Session session, EndpointConfig config) {
    if (session.isOpen()) {
      websocketService.addSocketSession(session);
    }
  }
}
