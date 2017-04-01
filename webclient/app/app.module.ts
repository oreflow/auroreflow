import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule }   from '@angular/forms';

import { LightbulbService } from './service/lightbulb.service';

import { AppComponent } from './app.component';
import { SingleLightbulbComponent } from './components/singlelightbulb/singlelightbulb.component';
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
    { path: 'singlelightbulb/:id', component: SingleLightbulbComponent },
    { path: 'renamelightbulbs', component: RenameLightbulbsComponent },
    { path: 'map', component: MapViewComponent },
    { path: '', component: LightbulblistComponent }
];

@NgModule({
    declarations: [
        AppComponent,
        SingleLightbulbComponent,
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
