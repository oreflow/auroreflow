package com.oreflow.auroreflow;

import com.google.inject.AbstractModule;
import com.oreflow.auroreflow.services.LightbulbService;
import com.oreflow.auroreflow.services.LightbulbDetectionService;
import com.oreflow.auroreflow.services.LightbulbSocketService;
import com.oreflow.auroreflow.services.WebsocketService;
import com.oreflow.auroreflow.websocket.LightbulbEndpoint;


public class AuroreflowModule extends AbstractModule {
  protected void configure() {
    bind(WebsocketService.class);
    bind(LightbulbSocketService.class);
    bind(LightbulbDetectionService.class);
    bind(LightbulbService.class);
    bind(LightbulbEndpoint.class);
  }
}
