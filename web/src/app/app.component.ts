import { Component } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { MatSidenav } from '@angular/material';

import '../assets/css/styles.css';
import { LightbulbService } from './service/lightbulb.service';
import { OnInit } from '@angular/core/src/metadata/lifecycle_hooks';

@Component({
  selector: 'auroreflow',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  sidenavOpen: boolean = false;
  constructor(private lightbulbService: LightbulbService) {}

  ngOnInit() {
    window.onfocus = () => {
      this.lightbulbService.connectWebsocket();
      this.lightbulbService.getLightbulbs(false);
    };
  }

  toggleSidenav(sidenav: MatSidenav) {
    sidenav.toggle();
  }

  powerOffAll() {
    this.lightbulbService.sendPowerOffAll();
  }
 }
