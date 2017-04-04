package com.oreflow.auroreflow;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.websocket.server.ServerEndpointConfig.Configurator;

public class AuroreflowConfigurator extends Configurator {

  @Inject
  private static Injector injector;

  public AuroreflowConfigurator() {
  }

  public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
    return injector.getInstance(endpointClass);
  }

}