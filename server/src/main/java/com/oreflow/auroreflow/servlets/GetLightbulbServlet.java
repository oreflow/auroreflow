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
package com.oreflow.auroreflow.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.services.LightbulbService;
import com.oreflow.auroreflow.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to get a single {@link Lightbulb} by Id.
 */
@Singleton
public class GetLightbulbServlet extends HttpServlet {
  private final LightbulbService lightbulbService;

  @Inject
  public GetLightbulbServlet(LightbulbService lightbulbService) {
    this.lightbulbService = lightbulbService;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    long lightbulbId = Long.parseLong(req.getPathInfo().replace("/", ""));
    Lightbulb lightbulb = lightbulbService.getLightbulb(lightbulbId);
    String jsonResponse = JsonUtil.toJSON(lightbulb);
    resp.setContentType("application/json");
    resp.getOutputStream().write(jsonResponse.getBytes());
  }
}
