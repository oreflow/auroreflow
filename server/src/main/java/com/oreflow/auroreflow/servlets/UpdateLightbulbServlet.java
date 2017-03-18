package com.oreflow.auroreflow.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.LightbulbRequest;
import com.oreflow.auroreflow.services.LightbulbService;
import com.oreflow.auroreflow.util.JsonUtil;
import com.oreflow.auroreflow.util.LightbulbMessages;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class UpdateLightbulbServlet extends HttpServlet {
  private final LightbulbService lightbulbService;

  @Inject
  public UpdateLightbulbServlet(LightbulbService lightbulbService) {
    this.lightbulbService = lightbulbService;
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    long lightbulbId = Long.parseLong(req.getPathInfo());
    LightbulbRequest lightbulbRequest = JsonUtil.parseLightbulbRequest(req.getReader());
    LightbulbMessages.validateLightbulbRequest(lightbulbRequest);
    System.out.printf("Got request with id %d, and message %s", lightbulbId, lightbulbRequest);
    lightbulbService.sendLightbulbRequest(lightbulbId, lightbulbRequest);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
