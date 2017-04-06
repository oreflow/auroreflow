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

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Service to manage Lightbulbs and their current states
 */
@Singleton
public class LightbulbService {
  private static final Logger logger = Logger.getLogger(LightbulbService.class.getName());

  private final WebsocketService websocketService;
  private final Map<Long, Lightbulb> lightbulbs;

  @Inject
  LightbulbService(WebsocketService websocketService) {
    this.websocketService = websocketService;
    lightbulbs = new ConcurrentHashMap<>();
  }

  /**
   * Adds a new {@link Lightbulb}
   */
  void putLightbulb(Lightbulb lightbulb) throws IOException {
    updateLightbulb(lightbulb);
  }

  /**
   * Gets a {@link Lightbulb} by Id, or throws exception if it does not exist
   */
  public Lightbulb getLightbulb(long id) {
    if (lightbulbs.containsKey(id)) {
      return lightbulbs.get(id);
    }
    throw new IllegalArgumentException("Lightbulb ID does not exist " + id);
  }

  /**
   * Updates the stored {@link Lightbulb} with information in given {@link LightbulbRequest}
   */
  void updateLightbulbWith(Lightbulb lightbulb, LightbulbRequest lightbulbRequest) {
    Lightbulb.Builder updatedLightbulb = lightbulb.toBuilder()
        .setLastChangeMillis(lightbulbRequest.getRequestTime())
        .setIsActive(true);
    switch (lightbulbRequest.getRequestTypeCase()) {
      case HSV_REQUEST:
        updateLightbulb(
            updatedLightbulb
                .setHue(lightbulbRequest.getHsvRequest().getHue())
                .setSat(lightbulbRequest.getHsvRequest().getSat())
                .setBright(lightbulbRequest.getHsvRequest().getBrightness())
                .setColorMode(Lightbulb.ColorMode.COLOR_MODE)
                .setPower(AuroreflowProto.Power.ON)
                .build());
        break;
      case CT_REQUEST:
        updateLightbulb(
            updatedLightbulb
                .setCt(lightbulbRequest.getCtRequest().getCt())
                .setBright(lightbulbRequest.getCtRequest().getBrightness())
                .setColorMode(Lightbulb.ColorMode.COLOR_TEMPERATURE_MODE)
                .setPower(AuroreflowProto.Power.ON)
                .build());
        break;
      case POWER_REQUEST:
        updateLightbulb(
            updatedLightbulb
                .setPower(lightbulbRequest.getPowerRequest().getPower())
                .build());
        break;
      case NAME_REQUEST:
        updateLightbulb(
            updatedLightbulb
                .setName(lightbulbRequest.getNameRequest().getName())
                .build());
        break;
      case REQUESTTYPE_NOT_SET:
        updateLightbulb(lightbulb.toBuilder().setIsActive(true).build());
        break;
    }
  }

  /**
   * Updates stored {@link Lightbulb} and triggers weboscket broadcast about the update
   */
  void updateLightbulb(Lightbulb lightbulb) {
    if(!lightbulbs.containsKey(lightbulb.getId())
        || !lightbulbs.get(lightbulb.getId()).equals(lightbulb)) {
      logger.log(Level.INFO, String.format("UPDATE ID: %d WITH STATUS: %b", lightbulb.getId(), lightbulb.getIsActive()));
      lightbulbs.put(lightbulb.getId(), lightbulb);
      websocketService.broadcastLightbulbUpdate(lightbulb);
    }
  }

  /**
   * Checks if the service currently has a lightbulb of provided ID
   */
  boolean hasLightbulb(long lightbulbId) {
    return lightbulbs.containsKey(lightbulbId);
  }

  /**
   * Gets a list of all {@link Lightbulb}s
   */
  public ImmutableCollection<Lightbulb> getAllLightbulbs() {
    return ImmutableList.copyOf(lightbulbs.values());
  }
}
