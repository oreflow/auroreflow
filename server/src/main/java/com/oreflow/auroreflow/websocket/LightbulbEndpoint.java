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
package com.oreflow.auroreflow.websocket;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.AuroreflowConfigurator;
import com.oreflow.auroreflow.services.WebsocketService;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


/**
 * Endpoint for websocket connections.
 */
@Singleton
@ServerEndpoint(value = "/lightbulbupdates", configurator = AuroreflowConfigurator.class)
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
