package com.oreflow.auroreflow.util;

import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbCommandResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class JsonUtil {
  private static final JsonFormat.Printer protobufJsonPrinter = JsonFormat.printer().includingDefaultValueFields();
  private static final JsonFormat.Parser protobufJsonParser = JsonFormat.parser();

  private static final JsonParser jsonParser = new JsonParser();
  private JsonUtil() {}


  /**
   * Creates a JsonArray from a given collection
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

  private final static Pattern commandResponseMatcher =
      Pattern.compile("\\{\"id\":[0-9]*, \"result\":\\[\"ok\"\\]\\}");
  public static boolean isCommandResponse(String message) {
    return commandResponseMatcher.matcher(message).matches();
  }

  /**
   * Parses a Lighbulb command response from JSON to {@link LightbulbCommandResponse}
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
