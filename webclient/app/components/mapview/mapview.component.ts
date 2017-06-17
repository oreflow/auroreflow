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
import {ApartmentService} from "../../service/apartment.service";
import {Lightbulb} from "../../model/lightbulb";

enum ToggleAble {
    LIVINGROOM = 1,
    BEDROOM = 2,
    LIVINGROOM_RIGHT = 3,
    LIVINGROOM_LEFT = 4,
    KITCHEN = 5,
    ENTRANCE = 6,
    HALLWAY = 7,
    BATHROOM = 8
}

@Component({
  template: require('./mapview.component.html'),
  styles: [require('./mapview.component.scss')],
})
export class MapViewComponent implements OnInit {
    private ToggleAble = ToggleAble;

    private ON_CT = 3000;
    private ON_BRIGHTNESS = 40;
    private OFF_POWER = 'OFF';

    private LIVINGROOM_BULB_IDS = [
        ApartmentService.LIVINGROOM_ROOF_BULB_ID_1,
        ApartmentService.LIVINGROOM_ROOF_BULB_ID_2
    ];
    private BEDROOM_BULB_IDS = [
        ApartmentService.BEDROOM_ROOF_BULB_ID_1,
        ApartmentService.BEDROOM_ROOF_BULB_ID_2
    ];

    private powerStatus: {[key: number]: boolean} = {};


    constructor(private lightbulbService: LightbulbService) {
        this.lightbulbService.getInitPromise().then(() => {
            const lightbulbs = this.lightbulbService.getListofLightbulbs();
            if(lightbulbs.length > 0) {
                lightbulbs.forEach((lightbulb: Lightbulb) => {
                    if(lightbulb.power === 'ON') {
                        switch (lightbulb.id) {
                            case ApartmentService.LIVINGROOM_RIGHT_BULB_ID:
                                this.powerStatus[ToggleAble.LIVINGROOM_RIGHT] = true;
                                break;
                            case ApartmentService.LIVINGROOM_LEFT_BULB_ID:
                                this.powerStatus[ToggleAble.LIVINGROOM_LEFT] = true;
                                break;
                            case ApartmentService.LIVINGROOM_ROOF_BULB_ID_1:
                            case ApartmentService.LIVINGROOM_ROOF_BULB_ID_2:
                                this.powerStatus[ToggleAble.LIVINGROOM] = true;
                                break;
                            case ApartmentService.BEDROOM_ROOF_BULB_ID_1:
                            case ApartmentService.BEDROOM_ROOF_BULB_ID_2:
                                this.powerStatus[ToggleAble.BEDROOM] = true;
                                break;
                            case ApartmentService.KITCHEN_BULB_ID:
                                this.powerStatus[ToggleAble.KITCHEN] = true;
                                return;
                            case ApartmentService.ENTRANCE_BULB_ID:
                                this.powerStatus[ToggleAble.ENTRANCE] = true;
                                return;
                            case ApartmentService.HALLWAY_BULB_ID:
                                this.powerStatus[ToggleAble.HALLWAY] = true;
                                return;
                            case ApartmentService.BATHROOM_BULB_ID:
                                this.powerStatus[ToggleAble.BATHROOM] = true;
                                return;
                        }
                    }
                });
            }
        });
    }

    ngOnInit(): void {
    }

    toggle(entity: ToggleAble) {
        this.powerStatus[entity] = !this.powerStatus[entity];
        switch (entity) {
            case ToggleAble.LIVINGROOM:
                this.sendUpdatesTo(this.LIVINGROOM_BULB_IDS, this.powerStatus[entity]);
                break;
            case ToggleAble.BEDROOM:
                this.sendUpdatesTo(this.BEDROOM_BULB_IDS, this.powerStatus[entity]);
                break;
            case ToggleAble.LIVINGROOM_RIGHT:
                this.sendUpdatesTo([ApartmentService.LIVINGROOM_RIGHT_BULB_ID], this.powerStatus[entity]);
                break;
            case ToggleAble.LIVINGROOM_LEFT:
                this.sendUpdatesTo([ApartmentService.LIVINGROOM_LEFT_BULB_ID], this.powerStatus[entity]);
                break;
            case ToggleAble.KITCHEN:
                this.sendUpdatesTo([ApartmentService.KITCHEN_BULB_ID], this.powerStatus[entity]);
                break;
            case ToggleAble.ENTRANCE:
                this.sendUpdatesTo([ApartmentService.ENTRANCE_BULB_ID], this.powerStatus[entity]);
                break;
            case ToggleAble.HALLWAY:
                this.sendUpdatesTo([ApartmentService.HALLWAY_BULB_ID], this.powerStatus[entity]);
                break;
            case ToggleAble.BATHROOM:
                this.sendUpdatesTo([ApartmentService.BATHROOM_BULB_ID], this.powerStatus[entity]);
                break;
        }
    }


    sendUpdatesTo(ids: string[], on: boolean) {
        this.lightbulbService.getListofLightbulbs()
            .filter(lightbulb => ids.indexOf(lightbulb.id) >= 0)
            .forEach(lightbulb => {
                if(on) {
                    lightbulb.ct = this.ON_CT;
                    lightbulb.bright = this.ON_BRIGHTNESS;
                    this.lightbulbService.sendCtUpdate(lightbulb);
                } else {
                    lightbulb.power = this.OFF_POWER;
                    this.lightbulbService.sendPowerUpdate(lightbulb);
                }
            });
    }
}