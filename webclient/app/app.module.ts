import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpModule } from '@angular/http';

import { LightbulbService } from './service/lightbulb.service';

import { AppComponent } from './app.component';
import { SinglebulbComponent } from './components/singlebulb/singlebulb.component';

const appRoutes: Routes = [
  { path: '*', component: AppComponent }
];

@NgModule({
    declarations: [
        AppComponent,
        SinglebulbComponent
    ],
    imports: [
        BrowserModule,
        RouterModule.forRoot(appRoutes),
        HttpModule
  ],
  providers: [
      LightbulbService
  ],
  bootstrap: [
      AppComponent
  ]
})
export class AppModule { }
