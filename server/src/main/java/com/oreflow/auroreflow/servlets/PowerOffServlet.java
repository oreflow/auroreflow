package com.oreflow.auroreflow.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.proto.AuroreflowProto.PowerRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.proto.AuroreflowProto.Power;
import com.oreflow.auroreflow.services.LightbulbService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class PowerOffServlet extends HttpServlet {
  private final LightbulbService lightbulbService;

  @Inject
  public PowerOffServlet(LightbulbService lightbulbService) {
    this.lightbulbService = lightbulbService;

  }
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    LightbulbRequest lightbulbRequest = LightbulbRequest.newBuilder()
        .setPowerRequest(
            PowerRequest.newBuilder()
                .setPower(Power.OFF))
        .build();

    for(Lightbulb lightbulb : lightbulbService.getActiveLightbulbs()) {
      lightbulbService.sendLightbulbRequest(lightbulb.getId(), lightbulbRequest);
    }
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
