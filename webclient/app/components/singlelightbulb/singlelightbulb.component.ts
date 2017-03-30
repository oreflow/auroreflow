import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";

import { LightbulbService } from '../../service/lightbulb.service';
import { Lightbulb } from '../../model/lightbulb';

@Component({
  template: require('./singlelightbulb.component.html'),
  styles: [require('./singlelightbulb.component.scss')],
})
export class SingleLightbulbComponent implements OnInit {
    private lightbulb : Lightbulb;
    toggleSwitchValue: boolean = true;

    constructor(
        private route: ActivatedRoute,
        private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        console.log("initing");
        console.log(this.route);
        this.route.params.subscribe(params => {
            let id = params['id'];
            this.lightbulbService.getLightbulb(id)
                .then(bulb => {
                    this.lightbulb = bulb;
                    console.log(this.lightbulb);
                });
        });
    }
    hsvChange(): void {
        this.lightbulbService.sendHsvUpdate(this.lightbulb);
    }
    ctChange(): void {
        this.lightbulbService.sendCtUpdate(this.lightbulb);
    }

    toggleHandler(toggled: boolean): void {
        console.log("toggled value change", toggled);
    }
}