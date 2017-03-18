import { Http, Response, Headers } from '@angular/http';
import { Injectable } from '@angular/core';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

import { Lightbulb } from '../model/lightbulb';
import {LightbulbRequest} from "../model/lightbulbrequest";

@Injectable()
export class LightbulbService {
    private PUT_HEADERS = new Headers({ 'Content-Type': 'application/json' });
    private lightbulbs: Lightbulb[] = [] ;

    constructor(private http: Http) {}

    getListofLightbulbs(): Lightbulb[] {
        let promise = this.http
            .get('lightbulb/list')
            .map((res: Response) => res.json() as Lightbulb[])
            .toPromise();
        promise.then(data => console.log(data));
        promise
            .then(lightbulbList => lightbulbList.forEach(lightbulb => this.putLightbulb(lightbulb)))
            .catch((err) => {
                console.log(err);
                this.putLightbulb(this.createMockLightbulb(5));

                setTimeout(() => {
                    this.putLightbulb(this.createMockLightbulb(8));
                },5000);
            });
        return this.lightbulbs;
    };

    getLightbulb(id: number): Promise<Lightbulb> {
        let existing = this.lightbulbs.find(lightbulb => lightbulb.id == id);
        if(existing) {
            return Promise.resolve(existing);
        }
        return this.http
            .get('lightbulb/get/' + id)
            .map((res: Response) => res.json().data as Lightbulb)
            .toPromise();
    }

    sendHsvUpdate(lightbulb: Lightbulb): void {
        let request: LightbulbRequest = {
            hsv_request: {
                hue: lightbulb.hue,
                sat: lightbulb.sat,
                brightness: lightbulb.bright,
            }
        };
        this.http
            .put('lightbulb/update/' + lightbulb.id, JSON.stringify(request), { headers: this.PUT_HEADERS })
            .map((res: Response) => res.json().data as Lightbulb[])
            .toPromise();

    }


    putLightbulb(lightbulb: Lightbulb) {
        let existingIndex = this.lightbulbs.findIndex(bulb => bulb.id === lightbulb.id);
        if(existingIndex >= 0) {
            this.lightbulbs[existingIndex] = lightbulb;
        } else {
            this.lightbulbs.push(lightbulb);
        }
    }


    createMockLightbulb(id: number): Lightbulb {
        return {
            id: id,
            model: "COLOR",
            location: "yeelight://192.168.0.179:55443",
            ip: "192.168.0.179",
            port: 55443,
            power: "ON",
            bright: 100,
            ct: 4000,
            hue: 359,
            sat: 100,
            is_active: true,
            name: "whatapp!" + id
        };
    }
}