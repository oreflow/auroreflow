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
package com.oreflow.auroreflow;

import com.google.inject.servlet.ServletModule;
import com.oreflow.auroreflow.servlets.*;

/**
 * Class to register and inject servlets for Auroreflow.
 */
public class AuroreflowServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    serve("/lightbulb/update/*").with(UpdateLightbulbServlet.class);
    serve("/lightbulb/get/*").with(GetLightbulbServlet.class);
    serve("/lightbulb/list").with(ListLightbulbServlet.class);
    serve("/poweroff").with(PowerOffAllServlet.class);
    requestStaticInjection(AuroreflowConfigurator.class);
  }
}
