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
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule }   from '@angular/forms';

import { LightbulbService } from './service/lightbulb.service';

import { AppComponent } from './app.component';
import { LightbulblistComponent } from './components/lightbulblist/lightbulblist.component';
import { ToggleSwitchComponent } from "./components/toggleswitch/toggleswitch.component";
import { HsvCtToggleComponent } from "./components/hsvcttoggle/hsvcttoggle.component";
import { HsvControlComponent } from "./components/hsvcontrol/hsvcontrol.component";
import { CtControlComponent } from "./components/ctcontrol/ctcontrol.component";
import { PowerControlComponent } from "./components/powercontrol/powercontrol.component";
import { SliderComponent } from "./components/slider/slider.component";
import { EditableNameComponent } from "./components/editablename/editablename.component";
import { RenameLightbulbsComponent } from "./components/renamelightbulbs/renamelightbulbs.component";
import {WebsocketService} from "./service/websocket.service";
import {MapViewComponent} from "./components/mapview/mapview.component";
import {ApartmentService} from "./service/apartment.service";

const appRoutes: Routes = [
    { path: 'rename', component: RenameLightbulbsComponent },
    { path: 'apartmentmap', component: MapViewComponent },
    { path: 'list', component: LightbulblistComponent },
    { path: '', component: LightbulblistComponent }
];

@NgModule({
    declarations: [
        AppComponent,
        LightbulblistComponent,
        RenameLightbulbsComponent,
        ToggleSwitchComponent,
        HsvCtToggleComponent,
        HsvControlComponent,
        CtControlComponent,
        EditableNameComponent,
        PowerControlComponent,
        SliderComponent,
        MapViewComponent,
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule.forRoot(appRoutes, { useHash: true }),
  ],
  providers: [
      LightbulbService,
      WebsocketService,
      ApartmentService,
  ],
  bootstrap: [
      AppComponent,
  ]
})
export class AppModule { }
