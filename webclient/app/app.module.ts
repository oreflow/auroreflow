import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule }   from '@angular/forms';

import { LightbulbService } from './service/lightbulb.service';

import { AppComponent } from './app.component';
import { SingleLightbulbComponent } from './components/singlelightbulb/singlelightbulb.component';
import { ApartmentComponent } from './components/apartment/apartment.component';

const appRoutes: Routes = [
  { path: 'singlelightbulb/:id', component: SingleLightbulbComponent },
  { path: '', component: ApartmentComponent }
];

@NgModule({
    declarations: [
        AppComponent,
        SingleLightbulbComponent,
        ApartmentComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule.forRoot(appRoutes),
  ],
  providers: [
      LightbulbService
  ],
  bootstrap: [
      AppComponent
  ]
})
export class AppModule { }
