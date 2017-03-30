package com.oreflow.auroreflow.services;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.util.LightbulbMessages;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


/**
 * Service to manage Lightbulbs
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
    final long lightbulbId = lightbulb.getId();
    if(lightbulbs.containsKey(lightbulbId)) {
      LightbulbRequest restoreRequest = LightbulbMessages.createRestoreRequest(lightbulbs.get(lightbulbId));
      updateLightbulbWith(lightbulb, restoreRequest);
    } else {
      updateLightbulb(lightbulb);
    }
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
   *  Updates the stored {@link Lightbulb} with the information in given {@link LightbulbRequest}
   */
  public void updateLightbulbWith(Lightbulb lightbulb, LightbulbRequest lightbulbRequest) {
    switch (lightbulbRequest.getRequestTypeCase()) {
      case HSV_REQUEST:
        updateLightbulb(
            lightbulb.toBuilder()
                .setHue(lightbulbRequest.getHsvRequest().getHue())
                .setSat(lightbulbRequest.getHsvRequest().getSat())
                .setBright(lightbulbRequest.getHsvRequest().getBrightness())
                .setColorMode(Lightbulb.ColorMode.COLOR_MODE)
                .setPower(AuroreflowProto.Power.ON)
                .setLastChangeMillis(lightbulbRequest.getRequestTime())
                .build());
        break;
      case CT_REQUEST:
        updateLightbulb(
            lightbulb.toBuilder()
                .setCt(lightbulbRequest.getCtRequest().getCt())
                .setBright(lightbulbRequest.getCtRequest().getBrightness())
                .setColorMode(Lightbulb.ColorMode.COLOR_TEMPERATURE_MODE)
                .setPower(AuroreflowProto.Power.ON)
                .setLastChangeMillis(lightbulbRequest.getRequestTime())
                .build());
        break;
      case POWER_REQUEST:
        updateLightbulb(
            lightbulb.toBuilder()
                .setPower(lightbulbRequest.getPowerRequest().getPower())
                .setLastChangeMillis(lightbulbRequest.getRequestTime())
                .build());
        break;
      case NAME_REQUEST:
        updateLightbulb(
            lightbulb.toBuilder()
                .setName(lightbulbRequest.getNameRequest().getName())
                .setLastChangeMillis(lightbulbRequest.getRequestTime())
                .build());
        break;
      case REQUESTTYPE_NOT_SET:
        updateLightbulb(lightbulb.toBuilder().setIsActive(true).build());
        break;
    }
  }

  /**
   * Updates stored {@link Lightbulb}
   */
  public void updateLightbulb(Lightbulb lightbulb) {
    if(!lightbulbs.containsKey(lightbulb.getId())
        || !lightbulbs.get(lightbulb.getId()).equals(lightbulb)) {
      lightbulbs.put(lightbulb.getId(), lightbulb);
      websocketService.broadcastLightbulbUpdate(lightbulb);
    }
  }

  /**
   * Gets a list of all {@link Lightbulb}s where is_active = true
   */
  public ImmutableCollection<Lightbulb> getActiveLightbulbs() {
    return lightbulbs.values().stream().filter(Lightbulb::getIsActive).collect(ImmutableList.toImmutableList());
  }

  /**
   * Gets a list of all {@link Lightbulb}s
   */
  public ImmutableCollection<Lightbulb> getAllLightbulbs() {
    return ImmutableList.copyOf(lightbulbs.values());
  }
}
