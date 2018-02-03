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
    MatSelectionList,
    MatSliderChange,
    MatSlideToggleChange
} from '@angular/material'

import { LightbulbService } from '../service/lightbulb.service';
import { Lightbulb } from '../model/lightbulb';

@Component({
    template: require('./selectionlist.component.html'),
    styles: [require('./selectionlist.component.css')],
})
export class SelectionlistComponent implements OnInit {
    private lightbulbs: Observable<Lightbulb[]>;
    power = 'on';
    ct = 3600;
    hue = 100;
    bright = 60;
    colorMode = 1;

    constructor(private lightbulbService: LightbulbService) { }

    ngOnInit(): void {
        this.lightbulbs = this.lightbulbService.getLightbulbs(true);
    }

    togglePower(selectionList: MatSelectionList) {
        if (this.power === 'on') {
            this.power = 'off';
        } else {
            this.power = 'on';
        }
        selectionList.selectedOptions.selected.forEach(lightbulbOption => {
            const lightbulb: Lightbulb = lightbulbOption.value;
            lightbulb.Power = this.power;
            this.lightbulbService.sendPowerUpdate(lightbulb);
        });
    }

    toggleColorMode(toggleChange: MatSlideToggleChange, selectionList: MatSelectionList) {
        this.power = 'on';
        if (toggleChange.checked) {
            this.colorMode = 2;
            selectionList.selectedOptions.selected.forEach(lightbulbOption => {
                const lightbulb: Lightbulb = lightbulbOption.value;
                lightbulb.Power = this.power;
                lightbulb.ColorMode = this.colorMode;
                this.lightbulbService.sendHsvUpdate(lightbulb);
            });
        } else {
            this.colorMode = 1;
            selectionList.selectedOptions.selected.forEach(lightbulbOption => {
                const lightbulb: Lightbulb = lightbulbOption.value;
                lightbulb.Power = this.power;
                lightbulb.ColorMode = this.colorMode;
                this.lightbulbService.sendCtUpdate(lightbulb);
            });
        }
    }

    updateHue(sliderChange: MatSliderChange, selectionList: MatSelectionList) {
        this.power = 'on';
        this.hue = sliderChange.value;
        selectionList.selectedOptions.selected.forEach(lightbulbOption => {
            const lightbulb: Lightbulb = lightbulbOption.value;
            lightbulb.Power = this.power;
            lightbulb.Sat = 100;
            lightbulb.Hue = this.hue;
            this.lightbulbService.sendHsvUpdate(lightbulb);
        });
    }

    updateCt(sliderChange: MatSliderChange, selectionList: MatSelectionList) {
        this.power = 'on';
        this.ct = sliderChange.value;
        selectionList.selectedOptions.selected.forEach(lightbulbOption => {
            const lightbulb: Lightbulb = lightbulbOption.value;
            lightbulb.Power = this.power;
            lightbulb.Ct = this.ct;
            this.lightbulbService.sendCtUpdate(lightbulb);
        });
    }

    updateBright(sliderChange: MatSliderChange, selectionList: MatSelectionList) {
        this.power = 'on';
        this.bright = sliderChange.value;
        selectionList.selectedOptions.selected.forEach(lightbulbOption => {
            const lightbulb: Lightbulb = lightbulbOption.value;
            lightbulb.Power = this.power;
            lightbulb.Bright = this.bright;
            if (this.colorMode == 1) {
                this.lightbulbService.sendCtUpdate(lightbulb);
            } else {
                this.lightbulbService.sendHsvUpdate(lightbulb);
            }
        });
    }
}