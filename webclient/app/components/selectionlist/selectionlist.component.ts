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

import { LightbulbService } from '../../service/lightbulb.service';
import { Lightbulb } from '../../model/lightbulb';

@Component({
  template: require('./selectionlist.component.html'),
  styles: [require('./selectionlist.component.scss')],
})
export class SelectionlistComponent implements OnInit{
    private lightbulbs : Lightbulb[];

    private CT_BACKGROUND = '-webkit-linear-gradient(left,#FF7A00, #FF8200, #FF9D3C, #FFA448, #FFAA54, #FFB569, #FFBC76, #FFC07F, #FFD4A8, #FFE5CE, #FFE5CE, #FFF0E8, #FFF6F6, #FFF9FF, #E2E8FF )';
    private HUE_BACKGROUND = '-webkit-linear-gradient(left, #FF0000, #FFFF00,#00FF00,#00FFFF,#0000FF,#FF00FF,#FF0000)';

    private SAT_FRAG_1 = '-webkit-linear-gradient(left, hsl(';
    private SAT_FRAG_2 = ',0%, 50%), hsl(';
    private SAT_FRAG_3 = ',100%, 50%))';
    private satBackground = '';
    private BRIGHT_BACKGROUND = '-webkit-linear-gradient(left, #555555, #FFFFFF)';

    private current = {
        hue : 180,
        sat : 90,
        ct : 3000,
        bright : 40,
        colorMode : 'COLOR_TEMPERATURE_MODE'
    };
    private selectedBulbs: Lightbulb[] = [];

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbs = this.lightbulbService.getListofLightbulbs();
    }

    selectionChange(lightbulb: Lightbulb) {
        let index = this.selectedBulbs.indexOf(lightbulb);
        if(index >= 0) {
            this.selectedBulbs.splice(index, 1);
        } else {
            this.selectedBulbs.push(lightbulb);
        }
    }

    hueChange(hue: number): void {
        this.current.hue = hue;
        this.sendUpdate();
    }

    satChange(sat: number): void {
        this.current.sat = sat;
        this.sendUpdate();
    }

    ctChange(ct: number): void {
        this.current.ct = ct;
        this.sendUpdate();
    }

    brightChange(bright: number): void {
        this.current.bright = bright;
        this.sendUpdate();
    }

    toggleMode(toggled: boolean): void {
        if(toggled) {
            this.current.colorMode = 'COLOR_MODE';
        } else {
            this.current.colorMode = 'COLOR_TEMPERATURE_MODE';
        }
    }
    sendUpdate() {
        this.selectedBulbs.forEach(bulb => {
            bulb.bright = this.current.bright;
            bulb.colorMode = this.current.colorMode;
            if(this.current.colorMode === 'COLOR_MODE') {
                bulb.hue = this.current.hue;
                bulb.sat = this.current.sat;
                this.lightbulbService.sendHsvUpdate(bulb);
            } else {
                bulb.ct = this.current.ct;
                this.lightbulbService.sendCtUpdate(bulb);
            }
        });
    }
}