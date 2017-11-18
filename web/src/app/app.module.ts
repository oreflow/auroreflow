import { NgModule } from '@angular/core';
import { BrowserModule }  from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule}  from '@angular/forms';
 
import { AppComponent } from './app.component';
import { LightbulblistComponent } from './lightbulblist/lightbulblist.component';
import { MapComponent } from './map/map.component';
import { PowerlistComponent } from './powerlist/powerlist.component';
import { SelectionlistComponent } from './selectionlist/selectionlist.component';
import { SidenavComponent } from './sidenav/sidenav.component';
import { LightbulbService } from './service/lightbulb.service';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { environment } from '../environments/environment';
import { 
  MatButtonModule,
  MatButtonToggleModule,
  MatGridListModule,
  MatIconModule,
  MatListModule, 
  MatSidenavModule, 
  MatSliderModule,
  MatSlideToggleModule,
  MatToolbarModule } from '@angular/material';
import { RouterModule, Routes } from '@angular/router';

const appRoutes: Routes = [
  { path: 'apartmentmap', component: MapComponent },
  { path: 'map', component: MapComponent },
  { path: 'power', component: PowerlistComponent },
  { path: 'selection', component: SelectionlistComponent },
  { path: '', component: LightbulblistComponent },
  { path: '**', redirectTo: '', pathMatch: 'full' },
];
 
@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatGridListModule,
    MatIconModule,
    MatListModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatToolbarModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes, { useHash: true }),
  ],
  declarations: [
    AppComponent,
    LightbulblistComponent,
    MapComponent,
    PowerlistComponent,
    SelectionlistComponent,
    SidenavComponent,
  ],
  providers: [
    LightbulbService
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
