import { Component, OnInit } from '@angular/core';

import { LightbulbService } from '../../service/lightbulb.service';
import { Lightbulb } from '../../model/lightbulb';

@Component({
  template: require('./renamelightbulbs.component.html'),
  styles: [require('./renamelightbulbs.component.scss')],
})
export class RenameLightbulbsComponent implements OnInit{
    private lightbulbs : Lightbulb[];

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbs = this.lightbulbService.getListofLightbulbs();
    }
}