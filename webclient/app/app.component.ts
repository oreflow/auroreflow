import { Component, OnInit } from '@angular/core';
import { LightbulbService } from './service/lightbulb.service';

@Component({
  selector: 'auroreflow-app',
  template: require('./app.component.html'),
  styles: [require('./app.component.scss')],
})
export class AppComponent implements OnInit{
    constructor(private lightbulbService: LightbulbService) {}
    ngOnInit(): void {
    }
}