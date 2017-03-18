
import { Component, OnInit } from '@angular/core';
import { LightbulbService } from '../../service/lightbulb.service';

@Component({
  template: require('./singlebulb.component.html'),
  styleUrls: ['./components/singlebulb/singlebulb.component.css'],
})
export class SinglebulbComponent implements OnInit{
    constructor(private lightbulbService: LightbulbService) {}
    ngOnInit(): void {
        console.log("initing");
    }
}