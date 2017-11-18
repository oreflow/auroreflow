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

import { Lightbulb } from '../model/lightbulb';
import {
    CtRequest,
    HsvRequest,
    NameRequest,
    PowerRequest
} from "../model/lightbulbrequest";
import { Subject } from 'rxjs/Subject';

@Injectable()
export class LightbulbService {
    private PUT_HEADERS = new Headers({ 'Content-Type': 'application/json' });
    lightbulbSubject: Subject<Lightbulb[]> = new Subject();
    lightbulbs: Lightbulb[];
    private websocket: WebSocket;

    constructor(private http: HttpClient) {
        this.connectWebsocket();
        this.getLightbulbs(true);
    }

    connectWebsocket() {
        if(this.websocket) {
            this.websocket.close();
        } 
        this.websocket = new WebSocket('ws://' + window.location.host + '/ws');
        this.websocket.onmessage = (message) => this.bulbUpdateHandler(message);
    }

    bulbUpdateHandler(message: MessageEvent) {
        const lightbulb: Lightbulb = JSON.parse(message.data);
        const existingBulb = this.lightbulbs.find(bulb => bulb.Id == lightbulb.Id);
        if(existingBulb) {
            if (JSON.stringify(existingBulb) == JSON.stringify(lightbulb)) {
                return;
            } 
            existingBulb.Power = lightbulb.Power;
            existingBulb.Bright = lightbulb.Bright;
            existingBulb.Ct = lightbulb.Ct;
            existingBulb.Hue = lightbulb.Hue;
            existingBulb.Sat = lightbulb.Sat;
            existingBulb.ColorMode = lightbulb.ColorMode;
            existingBulb.Name = lightbulb.Name;
            existingBulb.IsActive = lightbulb.IsActive;
        } else {
            this.lightbulbs.push(lightbulb);
        }
        this.lightbulbs = this.lightbulbs.sort((bulbA, bulbB) => bulbA.Name.localeCompare(bulbB.Name));
        this.lightbulbSubject.next(this.lightbulbs);
    }

    getLightbulbs(forceNext: boolean) {
        this.http.get<Lightbulb[]>('lightbulb/list').subscribe(data => {
            if (forceNext || !this.lightbulbs || data.length != this.lightbulbs.length) {
                this.lightbulbs = data.sort((bulbA, bulbB) => bulbA.Name.localeCompare(bulbB.Name));
                this.lightbulbSubject.next(this.lightbulbs);
            }
        });
        return this.lightbulbSubject.asObservable();
    }

    /**
     * Sends a Hsv update request using the HSV values provided in the Lightbulb object
     */
    sendHsvUpdate(lightbulb: Lightbulb): void {
        lightbulb.ColorMode = 2;
        const request: HsvRequest  = {
            Id: lightbulb.Id,
            Hue: lightbulb.Hue,
            Sat: lightbulb.Sat,
            Bright: lightbulb.Bright
        }
        this.http.put('lightbulb/update/hsv', JSON.stringify(request)).subscribe();
    }

    /**
     * Sends a Ct update request using the Ct values provided in the Lightbulb object
     */
    sendCtUpdate(lightbulb: Lightbulb): void {
        lightbulb.ColorMode = 1;
        const request: CtRequest  = {
            Id: lightbulb.Id,
            Ct: lightbulb.Ct,
            Bright: lightbulb.Bright
        }
        this.http.put('lightbulb/update/ct', JSON.stringify(request)).subscribe();
    }

    /**
     * Sends a Power update request using the power value provided in the Lightbulb object
     */
    sendPowerUpdate(lightbulb: Lightbulb): void {
        const request: PowerRequest = {
            Id: lightbulb.Id,
            Power: lightbulb.Power
        };
        this.http.put('lightbulb/update/power', JSON.stringify(request)).subscribe();
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
        this.http.get('poweroff').subscribe();
    }
}