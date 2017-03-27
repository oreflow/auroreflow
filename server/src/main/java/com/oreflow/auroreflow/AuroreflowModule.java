package com.oreflow.auroreflow;

import com.google.inject.AbstractModule;
import com.oreflow.auroreflow.services.LightbulbService;
import com.oreflow.auroreflow.services.LightbulbDetectionService;


public class AuroreflowModule extends AbstractModule {
    protected void configure() {
        bind(LightbulbDetectionService.class);
        bind(LightbulbService.class);
    }
}
