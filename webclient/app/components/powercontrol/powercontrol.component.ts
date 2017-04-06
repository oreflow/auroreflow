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
    selector: 'power-control',
    template: require('./powercontrol.component.html'),
    styles: [require('./powercontrol.component.scss')],
})
export class PowerControlComponent implements OnInit{
    @Input() id: string;
    private lightbulb: Lightbulb;

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id)
            .then(bulb => this.lightbulb = bulb);
    }

    powerToggle(): void {
        if(this.lightbulb.power === 'OFF') {
            this.lightbulb.power = 'ON';
        } else {
            this.lightbulb.power = 'OFF';
        }
        this.lightbulbService.sendPowerUpdate(this.lightbulb);
    }
}