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
import {PowerControlComponent} from "./components/powercontrol/powercontrol.component";
import {SliderComponent} from "./components/slider/slider.component";

const appRoutes: Routes = [
  { path: 'singlelightbulb/:id', component: SingleLightbulbComponent },
  { path: '', component: LightbulblistComponent }
];

@NgModule({
    declarations: [
        AppComponent,
        SingleLightbulbComponent,
        LightbulblistComponent,
        ToggleSwitchComponent,
        HsvCtToggleComponent,
        HsvControlComponent,
        CtControlComponent,
        PowerControlComponent,
        SliderComponent,
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule.forRoot(appRoutes),
  ],
  providers: [
      LightbulbService,
  ],
  bootstrap: [
      AppComponent,
  ]
})
export class AppModule { }
