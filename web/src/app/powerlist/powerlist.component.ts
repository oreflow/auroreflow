/**
 * Copyright 2017 Tim Malmström
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {
    MatSliderChange,
    MatSlideToggleChange
} from '@angular/material'

import { LightbulbService } from '../service/lightbulb.service';
import { Lightbulb } from '../model/lightbulb';

@Component({
    template: require('./powerlist.component.html'),
    styles: [require('./powerlist.component.css')],
})
export class PowerlistComponent implements OnInit {
    private lightbulbs: Observable<Lightbulb[]>;

    constructor(private lightbulbService: LightbulbService) { }

    ngOnInit(): void {
        this.lightbulbs = this.lightbulbService.getLightbulbs(true);
    }

    togglePower(lightbulb: Lightbulb) {
        if (lightbulb.Power === 'on') {
            lightbulb.Power = 'off';
            this.lightbulbService.sendPowerUpdate(lightbulb);
        } else {
            lightbulb.Ct = 3600;
            lightbulb.Bright = 50;
            lightbulb.Power = 'on';
            this.lightbulbService.sendCtUpdate(lightbulb);
        }
    }
}