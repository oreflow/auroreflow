package com.oreflow.auroreflow;

import com.google.inject.AbstractModule;
import com.oreflow.auroreflow.services.LightbulbService;
import com.oreflow.auroreflow.services.LightBulbDetectionService;


public class AuroreflowModule extends AbstractModule {
    protected void configure() {
        bind(LightBulbDetectionService.class);
        bind(LightbulbService.class);
    }
}
