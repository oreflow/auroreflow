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
import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Lightbulb} from "../../model/lightbulb";
import {LightbulbService} from "../../service/lightbulb.service";

@Component({
    selector: 'hsv-ct-toggle',
    template: require('./hsvcttoggle.component.html'),
    styles: [require('./hsvcttoggle.component.scss')],
})
export class HsvCtToggleComponent implements OnInit {

    @Input() id: string;
    private lightbulb: Lightbulb;

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id).then(bulb => this.lightbulb = bulb);
    }

    toggleHandler(toggled: boolean) {
        if(toggled) {
            this.lightbulb.colorMode = 'COLOR_MODE';
            this.lightbulbService.sendHsvUpdate(this.lightbulb);
        } else {
            this.lightbulb.colorMode = 'COLOR_TEMPERATURE_MODE';
            this.lightbulbService.sendCtUpdate(this.lightbulb);
        }
    }
}