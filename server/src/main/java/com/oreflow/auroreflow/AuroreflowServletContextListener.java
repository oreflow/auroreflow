package com.oreflow.auroreflow;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.oreflow.auroreflow.services.LightbulbDetectionService;
import com.oreflow.auroreflow.websocket.LightbulbEndpoint;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

public class AuroreflowServletContextListener extends GuiceServletContextListener {

  private Injector injector;
  protected Injector getInjector() {
    if(injector == null) {
      injector = Guice.createInjector(ImmutableList.of(
          new AuroreflowServletModule(),
          new AuroreflowModule()));
      injector.getInstance(LightbulbDetectionService.class);
    }
    return injector;
  }
}
