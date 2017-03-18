package com.oreflow.auroreflow;

import com.google.inject.servlet.ServletModule;
import com.oreflow.auroreflow.servlets.*;

public class AuroreflowServletModule extends ServletModule{
  @Override
  protected void configureServlets() {
    serve("/lightbulb/update/*").with(UpdateLightbulbServlet.class);
    serve("/lightbulb/get/*").with(GetLightbulbServlet.class);
    serve("/lightbulb/list").with(ListLightbulbServlet.class);
    serve("/poweroff").with(PowerOffAllServlet.class);
  }
}
