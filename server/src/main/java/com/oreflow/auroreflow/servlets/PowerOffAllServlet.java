package com.oreflow.auroreflow.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.proto.AuroreflowProto.PowerRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Power;
import com.oreflow.auroreflow.services.LightbulbService;
import com.oreflow.auroreflow.services.LightbulbSocketService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@Singleton
public class PowerOffAllServlet extends HttpServlet {
  private final LightbulbSocketService lightbulbSocketService;
  private final LightbulbService lightbulbService;

  @Inject
  public PowerOffAllServlet(LightbulbSocketService lightbulbSocketService, LightbulbService lightbulbService) {
    this.lightbulbSocketService = lightbulbSocketService;
    this.lightbulbService = lightbulbService;
  }
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    LightbulbRequest lightbulbRequest = LightbulbRequest.newBuilder()
        .setPowerRequest(
            PowerRequest.newBuilder()
                .setPower(Power.OFF))
        .setRequestTime(Instant.now().toEpochMilli())
        .build();

    for(Lightbulb lightbulb : lightbulbService.getAllLightbulbs()) {
      lightbulbSocketService.sendLightbulbRequest(lightbulb, lightbulbRequest);
    }
    resp.setStatus(HttpServletResponse.SC_OK);

    resp.setContentType("application/json");
    resp.getOutputStream().write("{}".getBytes());
  }
}
