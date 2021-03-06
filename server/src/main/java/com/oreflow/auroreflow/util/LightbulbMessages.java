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
package com.oreflow.auroreflow.util;

import com.google.common.base.Verify;
import com.google.gson.*;
import com.oreflow.auroreflow.proto.AuroreflowProto.Power;
import com.oreflow.auroreflow.proto.AuroreflowProto.PowerRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.CtRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.HsvRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Effect;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.Instant;

import static com.oreflow.auroreflow.util.JsonUtil.jsonArrayOf;

/**
 * Utility methods for command messages to {@link com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb}s.
 */
public final class LightbulbMessages {
  private LightbulbMessages() {}

  /**
   * Validates the properties of a {@link LightbulbRequest} so that they are within their bounds.
   */
  public static void validateLightbulbRequest(LightbulbRequest lightbulbRequest) {
    switch (lightbulbRequest.getRequestTypeCase()) {
      case HSV_REQUEST:
        Verify.verify(lightbulbRequest.getHsvRequest().getHue() >= 0 , "Hue has to be >= 0");
        Verify.verify(lightbulbRequest.getHsvRequest().getHue() <= 359 , "Hue has to be <= 359");
        Verify.verify(lightbulbRequest.getHsvRequest().getSat() >= 0, "Saturation has to be >= 0");
        Verify.verify(lightbulbRequest.getHsvRequest().getSat() <= 100 , "Saturation has to be <= 100");
        Verify.verify(lightbulbRequest.getHsvRequest().getBrightness() >= 1 , "Brightness has to be >= 1");
        Verify.verify(lightbulbRequest.getHsvRequest().getBrightness() <= 100 , "Brightness has to be <= 100");
        break;
      case CT_REQUEST:
        Verify.verify(lightbulbRequest.getCtRequest().getCt() >= 1700 , "CT has to be >= 1700");
        Verify.verify(lightbulbRequest.getCtRequest().getCt() <=  6500, "CT has to be <= 6500");
        Verify.verify(lightbulbRequest.getCtRequest().getBrightness() >= 1 , "Brightness has to be >= 1");
        Verify.verify(lightbulbRequest.getCtRequest().getBrightness() <= 100 , "Brightness has to be <= 100");
        break;
      case POWER_REQUEST:
        Verify.verify(lightbulbRequest.getPowerRequest().getPowerValue() > 0, "Power has to be ON or OFF");
        break;
      default:
    }
  }

  /**
   * Creates a Lightbulb-JSON String for the given LightbulbRequest
   */
  public static String createMessage(long id, LightbulbRequest lightbulbRequest) {
    switch (lightbulbRequest.getRequestTypeCase()) {
      case HSV_REQUEST:
        return createHsvMessage(
            id,
            lightbulbRequest.getHsvRequest().getHue(),
            lightbulbRequest.getHsvRequest().getSat(),
            lightbulbRequest.getHsvRequest().getBrightness());
      case CT_REQUEST:
        return createCtMessage(
            id,
            lightbulbRequest.getCtRequest().getCt(),
            lightbulbRequest.getCtRequest().getBrightness());
      case POWER_REQUEST:
        return createSetPower(id, lightbulbRequest.getPowerRequest().getPower());
      case NAME_REQUEST:
        return createSetName(id, lightbulbRequest.getNameRequest().getName());
      case REQUESTTYPE_NOT_SET:
      default:
        return createGetPower(id);
    }
  }

  /**
   * set_power 3 string(power) string(effect) int(duration)
   */
  private static String createSetPower(long id, Power power) {
    return createRequestObject(id,"set_power", jsonArrayOf(power, Effect.SUDDEN, 0));
  }

  /**
   * Set Hsv and brightness
   */
  private static String createHsvMessage(long id, int hue, int sat, int brightness) {
    return createRequestObject(id,"set_scene",
        jsonArrayOf("hsv", hue, sat, brightness));
  }

  /**
   * Set Ct and brightness
   */
  private static String createCtMessage(long id, int ct, int brightness) {
    return createRequestObject(id,"set_scene",
        jsonArrayOf("ct", ct, brightness));
  }

  /**
   * Get the power status of the lamp
   */
  private static String createGetPower(long id) {
    return createRequestObject(id, "get_prop", jsonArrayOf("power"));
  }

  /**
   * set_name 1 string(name)
   */
  private static String createSetName(long id, String name) {
    return createRequestObject(id,"set_name", jsonArrayOf(name));
  }

  /**
   * get_prop 1 ~ N * * * *
   */
  @Deprecated
  private static String createGetPropMessage(long id, String... property) {
    throw new NotImplementedException();
  }

  /**
   * set_ct_abx 3 int (ct_value) string(effect) int(duration)
   */
  @Deprecated
  private static String createSetCtAbx(long id, int ct, Effect effect, int duration) {
    return createRequestObject(id,"set_ct_abx", jsonArrayOf(ct, effect.toString(), duration));
  }

  /**
   * set_rgb 3 int(rgb_value) string(effect) int(duration)
   */
  @Deprecated
  private static String createSetRgb(long id, int rgb, Effect effect, int duration) {
    return createRequestObject(id,"set_rgb", jsonArrayOf(rgb, effect.toString(), duration));
  }

  /**
   * set_hsv 4 int(hue) int(sat) string(effect) int(duration)
   */
  @Deprecated
  private static String createSetHsv(long id, int hue, int sat, Effect effect, int duration) {
    return createRequestObject(id,"set_hsv", jsonArrayOf(hue, sat, effect.toString(), duration));
  }

  /**
   * set_bright 3 int(brightness) string(effect) int(duration)
   */
  @Deprecated
  private static String createSetBright(long id, int brightness, Effect effect, int duration) {
    return createRequestObject(id,"set_bright", jsonArrayOf(brightness, effect.toString(), duration));
  }

  /**
   * set_default 0
   */
  @Deprecated
  private static String createSetDefault(long id) {
    return createRequestObject(id,"set_default", jsonArrayOf());
  }

  /**
   * start_cf 3 int(count) int(action) string(flow_expression)
   */
  @Deprecated
  private static String createStartCf(long id, int count, int action, String flowExpression) {
    return createRequestObject(id,"start_cf", jsonArrayOf(count, action, flowExpression));
  }

  /**
   * stop_cf 0
   */
  @Deprecated
  private static String createStopCf(long id) {
    return createRequestObject(id,"stop_cf", jsonArrayOf());
  }

  /**
   * set_scene 3 ~ 4 string(class) int(val1) int(val2) * int(val3)
   */
  @Deprecated
  private static String createSetScene(long id) {
    throw new NotImplementedException();
  }

  /**
   * cron_add 2 int(type) int(value)
   */
  @Deprecated
  private static String createCronAdd(long id) {
    throw new NotImplementedException();
  }

  /**
   * cron_get 1 int(type)
   */
  @Deprecated
  private static String createCronGet(long id) {
    throw new NotImplementedException();
  }

  /**
   * cron_del 1 int(type)
   */
  @Deprecated
  private static String createCronDel(long id) {
    throw new NotImplementedException();
  }

  /**
   * set_adjust 2 string(action) string(prop)
   */
  @Deprecated
  private static String createSetAdjust(long id) {
    throw new NotImplementedException();
  }

  /**
   * set_music 1 ~ 3 int(action) string(host) int(port)
   */
  @Deprecated
  private static String createSetMusic(long id) {
    throw new NotImplementedException();
  }

  /**
   * Builds the a JSON Request string from given id, method and params shaped as
   * {
   *   id: @param id,
   *   method: @param method,
   *   params: [param0, param1, ..., paramN]
   * }
   */
  private static String createRequestObject(long id, String method, JsonArray params) {
    JsonObject json = new JsonObject();
    json.addProperty("id", id);
    json.addProperty("method", method);
    json.add("params", params);
    return json.toString() + "\r\n";
  }

  /**
   * Takes in a {@link Lightbulb} and creates a {@link LightbulbRequest} to restore its current state.
   */
  public static LightbulbRequest createRestoreRequest(Lightbulb lightbulb) {
    if(lightbulb.getPower().equals(Power.OFF)) {
      return LightbulbRequest.newBuilder().setPowerRequest(PowerRequest.newBuilder().setPower(Power.OFF)).build();
    }
    switch (lightbulb.getColorMode()) {
      case COLOR_TEMPERATURE_MODE:
        return LightbulbRequest.newBuilder()
            .setCtRequest(
                CtRequest.newBuilder()
                    .setCt(lightbulb.getCt())
                    .setBrightness(lightbulb.getBright()))
            .setRequestTime(Instant.now().toEpochMilli())
            .build();
      case COLOR_MODE:
        return LightbulbRequest.newBuilder()
            .setHsvRequest(
                HsvRequest.newBuilder()
                    .setHue(lightbulb.getHue())
                    .setSat(lightbulb.getSat())
                    .setBrightness(lightbulb.getBright()))
            .setRequestTime(Instant.now().toEpochMilli())
            .build();
    }
    throw new IllegalArgumentException("Cannot restore state of lighbulb with power on and non CT or HSV state");
  }
}
