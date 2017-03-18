package com.oreflow.auroreflow;

import com.google.inject.servlet.ServletModule;
import com.oreflow.auroreflow.servlets.*;

public class AuroreflowServletModule extends ServletModule{
  @Override
  protected void configureServlets() {
    serve("/lightbulb/*").with(UpdateLightbulbServlet.class);
    serve("/lightbulb/list").with(ListLightbulbServlet.class);
    serve("/poweroff").with(PowerOffServlet.class);
  }
}
