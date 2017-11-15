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

const ROOM_BY_LIGHTBULB_ID = {
    56238300: 'living-room-left',
    56285014: 'living-room-right',
    56313741: 'living-room',
    56279812: 'living-room',
    56310745: 'kitchen',
    56276376: 'bedroom',
    56312564: 'bedroom',
    56232341: 'hallway',
    56310674: 'entrance',
    56229429: 'bathroom'
};

@Component({
    template: require('./map.component.html'),
    styles: [require('./map.component.css')],
})
export class MapComponent implements OnInit {
    lightstatus: { [room: string]: boolean } = {
        'living-room': false,
        'living-room-right': false,
        'living-room-left': false,
        'kitchen': false,
        'bedroom': false,
        'hallway': false,
        'entrance': false,
        'bathroom': false,
    };
    lightbulbsByRoom: {[room: string]: Lightbulb[]} = {};

    constructor(private lightbulbService: LightbulbService) { }

    ngOnInit(): void {
        this.lightbulbService.getLightbulbs(true)
            .subscribe(lightbulbList => {
                this.lightbulbsByRoom = {};
                lightbulbList.forEach(lightbulb => {
                    const room = ROOM_BY_LIGHTBULB_ID[lightbulb.Id];
                    if (!this.lightbulbsByRoom[room]) {
                        this.lightbulbsByRoom[room] = [];
                        this.lightstatus[room] = false;
                    }
                    this.lightbulbsByRoom[room].push(lightbulb);
                    if (lightbulb.Power === 'on') {
                        this.lightstatus[room] = true;
                    }
                })
            });
    }

    toggleRoom(room: string) {
        this.lightstatus[room] = !this.lightstatus[room];
        this.lightbulbsByRoom[room].forEach(lightbulb => {
            if (this.lightstatus[room]) {
                lightbulb.Power = 'on';
                lightbulb.Ct = 3600;
                lightbulb.Bright = 50;
                this.lightbulbService.sendCtUpdate(lightbulb);
            } else {
                lightbulb.Power = 'off';
                this.lightbulbService.sendPowerUpdate(lightbulb);
            }
        })
    }
}