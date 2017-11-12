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
    template: require('./lightbulblist.component.html'),
    styles: [require('./lightbulblist.component.css')],
})
export class LightbulblistComponent implements OnInit {
    private lightbulbs: Observable<Lightbulb[]>;

    constructor(private lightbulbService: LightbulbService) { }

    ngOnInit(): void {
        this.lightbulbs = this.lightbulbService.getLightbulbs();
    }

    togglePower(lightbulb: Lightbulb) {
        if (lightbulb.Power === 'on') {
            lightbulb.Power = 'off';
        } else {
            lightbulb.Power = 'on';
        }
        this.lightbulbService.sendPowerUpdate(lightbulb);
    }

    toggleColorMode(toggleChange: MatSlideToggleChange, lightbulb: Lightbulb) {
        lightbulb.Power = 'on';
        if (toggleChange.checked) {
            lightbulb.ColorMode = 3;
            this.lightbulbService.sendHsvUpdate(lightbulb);
        } else {
            lightbulb.ColorMode = 2;
            this.lightbulbService.sendCtUpdate(lightbulb);
        }
    }

    updateHue(sliderChange: MatSliderChange, lightbulb: Lightbulb) {
        lightbulb.Power = 'on';
        lightbulb.Sat = 100;
        lightbulb.Hue = sliderChange.value;
        this.lightbulbService.sendHsvUpdate(lightbulb);
    }

    updateCt(sliderChange: MatSliderChange, lightbulb: Lightbulb) {
        lightbulb.Power = 'on';
        lightbulb.Ct = sliderChange.value;
        this.lightbulbService.sendCtUpdate(lightbulb);
    }

    updateBright(sliderChange: MatSliderChange, lightbulb: Lightbulb) {
        lightbulb.Power = 'on';
        lightbulb.Bright = sliderChange.value;
        if(lightbulb.ColorMode == 2) {
            this.lightbulbService.sendCtUpdate(lightbulb);
        } else {
            this.lightbulbService.sendHsvUpdate(lightbulb);
        }
    }
}