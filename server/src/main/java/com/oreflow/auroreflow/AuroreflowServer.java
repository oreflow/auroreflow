package com.oreflow.auroreflow;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.oreflow.auroreflow.services.LightBulbDetectionService;

public class AuroreflowServer extends GuiceServletContextListener {
  protected Injector getInjector() {
    Injector injector = Guice.createInjector(ImmutableList.of(
        new AuroreflowServletModule(),
        new AuroreflowModule()));
    injector.getInstance(LightBulbDetectionService.class);
    return injector;
  }
}
