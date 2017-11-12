import { Component } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import '../assets/css/styles.css';
import { LightbulbService } from './service/lightbulb.service';

@Component({
  selector: 'auroreflow',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  sidenavOpen: boolean = false;
  constructor(private lightbulbService: LightbulbService) {}

  toggleSidenav() {
    this.sidenavOpen = !this.sidenavOpen;
  }

  powerOffAll() {
    this.lightbulbService.sendPowerOffAll();
  }

  sidenavOpenedChanged(open: boolean) {
    console.log(open)
    this.sidenavOpen = open;
  }
 }
