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
import {Component, Input, OnInit} from '@angular/core';
import {Lightbulb} from "../../model/lightbulb";
import {LightbulbService} from "../../service/lightbulb.service";

@Component({
    selector: 'ct-control',
    template: require('./ctcontrol.component.html'),
    styles: [require('./ctcontrol.component.scss')],
})
export class CtControlComponent implements OnInit {
    @Input() id: string;
    private lightbulb: Lightbulb;
    private CT_BACKGROUND = '-webkit-linear-gradient(left,#FF7A00, #FF8200, #FF9D3C, #FFA448, #FFAA54, #FFB569, #FFBC76, #FFC07F, #FFD4A8, #FFE5CE, #FFE5CE, #FFF0E8, #FFF6F6, #FFF9FF, #E2E8FF )';
    private BRIGHT_BACKGROUND = '-webkit-linear-gradient(left, #555555, #FFFFFF)';

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id)
            .then(bulb => this.lightbulb = bulb);
    }

    sendCtChange() {
        this.lightbulbService.sendCtUpdate(this.lightbulb);
    }

    ctChange(ct: number): void {
        this.lightbulb.ct = ct;
        this.sendCtChange();
    }

    brightnessChange(brightness: number): void {
        this.lightbulb.bright = brightness;
        this.sendCtChange();
    }
}


