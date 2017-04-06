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

import com.google.inject.AbstractModule;
import com.oreflow.auroreflow.services.LightbulbService;
import com.oreflow.auroreflow.services.LightbulbDetectionService;
import com.oreflow.auroreflow.services.LightbulbSocketService;
import com.oreflow.auroreflow.services.WebsocketService;
import com.oreflow.auroreflow.websocket.LightbulbEndpoint;


public class AuroreflowModule extends AbstractModule {
  protected void configure() {
    bind(WebsocketService.class);
    bind(LightbulbSocketService.class);
    bind(LightbulbDetectionService.class);
    bind(LightbulbService.class);
    bind(LightbulbEndpoint.class);
  }
}
