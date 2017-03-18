import { Component, OnInit } from '@angular/core';

import { LightbulbService } from '../../service/lightbulb.service';
import { Lightbulb } from '../../model/lightbulb';

@Component({
  template: require('./apartment.component.html'),
  styles: [require('./apartment.component.scss')],
})
export class ApartmentComponent implements OnInit{
    private lightbulbs : Lightbulb[];

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbs = this.lightbulbService.getListofLightbulbs();
    }
}