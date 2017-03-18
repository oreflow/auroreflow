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
        ImmutableCollection<Lightbulb> lightbulbs = lightbulbService.getActiveLightbulbs();
        String jsonResponseString = jsonArrayOf(lightbulbs).toString();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(jsonResponseString.getBytes());
    }
}
