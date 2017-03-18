
import { Component, OnInit } from '@angular/core';
import { LightbulbService } from '../../service/lightbulb.service';

@Component({
  template: require('./singlebulb.component.html'),
  styles: [require('./singlebulb.component.scss')],
})
export class SinglebulbComponent implements OnInit{
    constructor(private lightbulbService: LightbulbService) {}
    ngOnInit(): void {
        console.log("initing");
    }
}