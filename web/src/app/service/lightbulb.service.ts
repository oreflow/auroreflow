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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';


import { Lightbulb } from '../model/lightbulb';
import {
    CtRequest,
    HsvRequest,
    NameRequest,
    PowerRequest
} from "../model/lightbulbrequest";

@Injectable()
export class LightbulbService {
    private PUT_HEADERS = new Headers({ 'Content-Type': 'application/json' });
    lightbulbs: Observable<Lightbulb[]>;

    constructor(private http: HttpClient) {
        //this.lightbulbs = Observable.of<any>([{"Id":56285014,"Location":"192.168.0.208:55443","Power":"on","Bright":40,"Ct":4117,"Hue":193,"Sat":90,"ColorMode":2,"Name":"Livingroom(right)","IsActive":true,"LastChangeMillis":1510506754}, {"Id":56285014,"Location":"192.168.0.208:55443","Power":"on","Bright":40,"Ct":4117,"Hue":193,"Sat":90,"ColorMode":2,"Name":"Livingroom(left)","IsActive":true,"LastChangeMillis":1510506754}]);
        this.lightbulbs = http.get<Lightbulb[]>('lightbulb/list');
    }

    getLightbulbs() {
        return this.lightbulbs;
    }

    /**
     * Sends a Hsv update request using the HSV values provided in the Lightbulb object
     */
    sendHsvUpdate(lightbulb: Lightbulb): void {
        const request: HsvRequest  = {
            Id: lightbulb.Id,
            Hue: lightbulb.Hue,
            Sat: lightbulb.Sat,
            Bright: lightbulb.Bright
        }
        this.http.put('lightbulb/update/hsv', JSON.stringify(request)).toPromise();
    }

    /**
     * Sends a Ct update request using the Ct values provided in the Lightbulb object
     */
    sendCtUpdate(lightbulb: Lightbulb): void {
        const request: CtRequest  = {
            Id: lightbulb.Id,
            Ct: lightbulb.Ct,
            Bright: lightbulb.Bright
        }
        this.http.put('lightbulb/update/ct', JSON.stringify(request)).toPromise();
    }

    /**
     * Sends a Power update request using the power value provided in the Lightbulb object
     */
    sendPowerUpdate(lightbulb: Lightbulb): void {
        const request: PowerRequest = {
            Id: lightbulb.Id,
            Power: lightbulb.Power
        };
        this.http.put('lightbulb/update/power', JSON.stringify(request)).toPromise();
    }

    /**
     * Sends a Name update request using the name value provided in the Lightbulb object
     */
    sendNameUpdate(lightbulb: Lightbulb): void {
    }

    /**
     * Sends a power off all bulbs request
     */
    sendPowerOffAll(): void {
        this.http.get('poweroff').toPromise();
    }
}