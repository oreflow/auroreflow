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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbCommandResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class JsonUtil {
  private static final JsonFormat.Printer protobufJsonPrinter = JsonFormat.printer().includingDefaultValueFields();
  private static final JsonFormat.Parser protobufJsonParser = JsonFormat.parser();
  private final static Pattern commandResponseMatcher =
      Pattern.compile("\\{\"id\":[0-9]*, \"result\":\\[\"ok\"\\]\\}");

  private static final JsonParser jsonParser = new JsonParser();
  private JsonUtil() {}


  /**
   * Creates a JsonArray from a given collection of {@link Object}s.
   */
  public static JsonArray jsonArrayOf(Object... elements) {
    JsonArray jsonArray = new JsonArray();
    for (Object element : elements) {
      if (element instanceof Boolean) {
        jsonArray.add((Boolean) element);
      } else if (element instanceof Character) {
        jsonArray.add((Character) element);
      } else if (element instanceof JsonElement) {
        jsonArray.add((JsonElement) element);
      } else if (element instanceof Number) {
        jsonArray.add((Number) element);
      } else if (element instanceof String) {
        jsonArray.add(((String) element));
      } else if (element instanceof Enum) {
        jsonArray.add(element.toString().toLowerCase());
      } else if (element instanceof Message) {
        String protobufJsonString = null;
        try {
          protobufJsonString = protobufJsonPrinter.print((Message) element);
        } catch (InvalidProtocolBufferException e) {
          e.printStackTrace();
        }
        jsonArray.add(jsonParser.parse(protobufJsonString));
      } else {
        throw new IllegalArgumentException("Tried to Json-serialize unsupported type " + element.getClass().getName());
      }
    }
    return jsonArray;
  }


  /**
   * Validates whether a message is a {@link com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb}
   * command response.
   */
  public static boolean isCommandResponse(String message) {
    return commandResponseMatcher.matcher(message).matches();
  }

  /**
   * Parses a Lighbulb command response from JSON to its requestId
   */
  public static long parseCommandResponseId(String responseMessage) {
    return new JsonParser().parse(responseMessage).getAsJsonObject().get("id").getAsLong();
  }

  /**
   * Parses a JSON LightbulbRequest to {@link LightbulbRequest}
   */
  public static LightbulbRequest parseLightbulbRequest(BufferedReader message) throws IOException {
    LightbulbRequest.Builder builder = LightbulbRequest.newBuilder();
    protobufJsonParser.merge(message.lines().collect(Collectors.joining()), builder);
    return builder.build();
  }

  /**
   * Formats a protobuf {@link Message} as a JSON String
   */
  public static String toJSON(Message message) {
    try {
      return protobufJsonPrinter.print(message);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
      return "";
    }
  }
}
