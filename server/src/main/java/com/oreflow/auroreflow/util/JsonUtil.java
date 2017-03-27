package com.oreflow.auroreflow.util;

import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbCommandResponse;
import com.oreflow.auroreflow.proto.AuroreflowProto.NameRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class JsonUtil {
  private static final JsonFormat protobufJsonFormatter = new JsonFormat();
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
        String protobufJsonString = protobufJsonFormatter.printToString((Message) element);
        jsonArray.add(jsonParser.parse(protobufJsonString));
      } else {
        throw new IllegalArgumentException("Tried to Json-serialize unsupported type " + element.getClass().getName());
      }
    }
    return jsonArray;
  }

  /**
   * Parses a Lighbulb command response from JSON to {@link LightbulbCommandResponse}
   */
  public static LightbulbCommandResponse parseCommandResponse(String responseMessage) throws IOException {
    InputStream messageStream = new ByteArrayInputStream(responseMessage.getBytes());
    LightbulbCommandResponse.Builder builder = LightbulbCommandResponse.newBuilder();
    protobufJsonFormatter.merge(messageStream, builder);
    return builder.build();
  }

  /**
   * Parses a JSON LightbulbRequest to {@link LightbulbRequest}
   */
  public static LightbulbRequest parseLightbulbRequest(BufferedReader message) throws IOException {
    InputStream targetStream = new ByteArrayInputStream(CharStreams.toString(message).getBytes());
    LightbulbRequest.Builder builder = LightbulbRequest.newBuilder();
    protobufJsonFormatter.merge(targetStream, builder);
    return builder.build();
  }

  /**
   * Formats a protobuf {@link Message} as a JSON String
   */
  public static String toJSON(Message message) {
    return protobufJsonFormatter.printToString(message);
  }
}
