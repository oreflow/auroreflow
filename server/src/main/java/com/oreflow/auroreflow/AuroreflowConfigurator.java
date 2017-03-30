package com.oreflow.auroreflow;

import com.google.inject.Injector;

import javax.websocket.server.ServerEndpointConfig.Configurator;

public class AuroreflowConfigurator extends Configurator {

    private final Injector injector;

    public AuroreflowConfigurator(Injector injector) {
        this.injector = injector;
    }

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        System.out.println("Get endpoint for " + endpointClass);
        return injector.getInstance(endpointClass);
    }

}