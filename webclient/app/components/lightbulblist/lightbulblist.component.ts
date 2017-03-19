import { Component, OnInit } from '@angular/core';

import { LightbulbService } from '../../service/lightbulb.service';
import { Lightbulb } from '../../model/lightbulb';

@Component({
  template: require('./lightbulblist.component.html'),
  styles: [require('./lightbulblist.component.scss')],
})
export class LightbulblistComponent implements OnInit{
    private lightbulbs : Lightbulb[];

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbs = this.lightbulbService.getListofLightbulbs();
    }
}