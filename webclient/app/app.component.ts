import { Component, OnInit } from '@angular/core';
import { LightbulbService } from './service/lightbulb.service';

@Component({
  selector: 'auroreflow-app',
  template: require('./app.component.html'),
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit{
    constructor(private lightbulbService: LightbulbService) {}
    ngOnInit(): void {
        console.log("initing");
        this.lightbulbService.getListofLightbulbs();
    }
}