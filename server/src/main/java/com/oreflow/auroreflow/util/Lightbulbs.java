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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb.ColorMode;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb.Model;
import com.oreflow.auroreflow.proto.AuroreflowProto.Power;

import java.io.BufferedReader;
import java.io.IOException;


/**
 * Utility class for prot
 */
public class Lightbulbs {
  private static final String RESPONSE_FIRST_LINE = "HTTP/1.1 200 OK";
  private static final String NOTIFY_FIRST_LINE = "NOTIFY * HTTP/1.1";

  public static boolean isBulbAdvertisement(byte[] input) throws IOException {
    String messageString = new String(input);
    BufferedReader messageReader = CharSource.wrap(messageString).openBufferedStream();
    String firstLine = messageReader.readLine();
    return firstLine.equals(RESPONSE_FIRST_LINE) || firstLine.equals(NOTIFY_FIRST_LINE);
  }

  public static Lightbulb parseAdvertisement(byte[] advertisement) throws IOException {
    BufferedReader messageReader = CharSource.wrap(new String(advertisement)).openBufferedStream();
    Lightbulb.Builder lightbulbBuilder = Lightbulb.newBuilder().setIsActive(true);
    String line;
    while((line = messageReader.readLine()) != null) {
      ImmutableList<String> split = ImmutableList.copyOf(line.split(":"))
          .stream()
          .map(String::trim)
          .collect(ImmutableList.toImmutableList());
      switch (split.get(0)) {
        case "Location":
          lightbulbBuilder.setLocation(Joiner.on(':').join(split.subList(1, split.size())));
          lightbulbBuilder.setIp(split.get(2).replace("//", ""));
          lightbulbBuilder.setPort(Integer.parseInt(split.get(3)));
          break;
        case "id":
          lightbulbBuilder.setId(Long.decode(split.get(1)));
          break;
        case "model":
          switch (split.get(1)) {
            case "color":
              lightbulbBuilder.setModel(Model.COLOR);
              break;
            case "mono":
              lightbulbBuilder.setModel(Model.MONO);
              break;
            default:
              throw new IllegalArgumentException();
          }
          break;
        case "power":
          switch (split.get(1)) {
            case "on":
              lightbulbBuilder.setPower(Power.ON);
              break;
            case "off":
              lightbulbBuilder.setPower(Power.OFF);
              break;
            default:
              throw new IllegalArgumentException();
          }
          break;
        case "bright":
          lightbulbBuilder.setBright(Integer.parseInt(split.get(1)));
          break;
        case "ct":
          lightbulbBuilder.setCt(Integer.parseInt(split.get(1)));
          break;
        case "hue":
          lightbulbBuilder.setHue(Integer.parseInt(split.get(1)));
          break;
        case "sat":
          lightbulbBuilder.setSat(Integer.parseInt(split.get(1)));
          break;
        case "name":
          lightbulbBuilder.setName(split.get(1));
          break;
        case "color_mode":
          if(Integer.parseInt(split.get(1)) == 2) {
            lightbulbBuilder.setColorMode(ColorMode.COLOR_TEMPERATURE_MODE);
          } else {
            lightbulbBuilder.setColorMode(ColorMode.COLOR_MODE);
          }
          break;
      }
    }
    return lightbulbBuilder.build();
  }
}
