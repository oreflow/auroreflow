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

import com.google.common.collect.ImmutableCollection;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.oreflow.auroreflow.proto.AuroreflowProto.Lightbulb;
import com.oreflow.auroreflow.services.LightbulbService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.oreflow.auroreflow.util.JsonUtil.jsonArrayOf;

@Singleton
public class ListLightbulbServlet extends HttpServlet {
    private final LightbulbService lightbulbService;

    @Inject
    public ListLightbulbServlet(LightbulbService lightbulbService) {
        this.lightbulbService = lightbulbService;

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ImmutableCollection<Lightbulb> lightbulbs = lightbulbService.getAllLightbulbs();
        lightbulbs.forEach(System.out::println);
        String jsonResponse = jsonArrayOf(lightbulbs.toArray()).toString();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonResponse.getBytes());
    }
}
