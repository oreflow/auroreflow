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

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.oreflow.auroreflow.services.LightbulbDetectionService;

/**
 * Main server of Auroreflow.
 */
public class AuroreflowServer extends GuiceServletContextListener {
  protected Injector getInjector() {
      Injector injector = Guice.createInjector(ImmutableList.of(
          new AuroreflowServletModule(),
          new AuroreflowModule()));
      injector.getInstance(LightbulbDetectionService.class);
      return injector;
  }

  public static void main(String [] args) {
    AuroreflowServer auroreflowServer  = new AuroreflowServer();
    auroreflowServer.getInjector();
  }
}
