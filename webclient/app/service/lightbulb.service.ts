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

    /**
     * Gets a list of currently active lightbulbs
     */
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
                    this.putLightbulb(this.createMockLightbulb(13));
                },2000);
            });
        return this.lightbulbs;
    };

    /**
     * Gets a specific lightbulb by ID
     */
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

    /**
     * Sends a Hsv update request using the HSV values provided in the Lightbulb object
     */
    sendHsvUpdate(lightbulb: Lightbulb): void {
        this.sendUpdateRequest(lightbulb.id,
            {
                hsv_request: {
                    hue: lightbulb.hue,
                    sat: lightbulb.sat,
                    brightness: lightbulb.bright,
                }
            });
    }
    /**
     * Sends a Ct update request using the Ct values provided in the Lightbulb object
     */
    sendCtUpdate(lightbulb: Lightbulb): void {
        this.sendUpdateRequest(lightbulb.id,
            {
                ct_request: {
                    ct: lightbulb.ct,
                    brightness: lightbulb.bright,
                }
            });
    }
    /**
     * Sends a Power update request using the power value provided in the Lightbulb object
     */
    sendPowerUpdate(lightbulb: Lightbulb): void {
        this.sendUpdateRequest(lightbulb.id,
            {
                power_request: {
                    power: lightbulb.power
                }
            });
    }
    /**
     * Sends a Name update request using the name value provided in the Lightbulb object
     */
    sendNameUpdate(lightbulb: Lightbulb): void {
        this.sendUpdateRequest(lightbulb.id,
            {
                name_request: {
                    name: lightbulb.name
                }
            });
    }

    /**
     * Sends a provided LightbulbRequest to the API
     */
    private sendUpdateRequest(id: number, request: LightbulbRequest): void {
        this.http
            .put('lightbulb/update/' + id,
                JSON.stringify(request),
                { headers: this.PUT_HEADERS })
            .map((res: Response) => res.json().data as Lightbulb[])
            .toPromise();
    }


    /**
     * Puts a lightbulb into the list of lightbulbs, replaces existing lightbulb if it already exists
     * //TODO, merge instead of replace to not loose object references
     */
    putLightbulb(lightbulb: Lightbulb) {
        let existingIndex = this.lightbulbs.findIndex(bulb => bulb.id === lightbulb.id);
        if(existingIndex >= 0) {
            this.lightbulbs[existingIndex] = lightbulb;
        } else {
            this.lightbulbs.push(lightbulb);
        }
    }


    createMockLightbulb(id: number): Lightbulb {
        console.log('creating bulb with ID', id);
        return {
            id: id,
            model: id > 10 ? "COLOR" : "MONO",
            location: "yeelight://192.168.0.179:55443",
            ip: "192.168.0.179",
            port: 55443,
            power: "ON",
            bright: 100,
            ct: 4000,
            hue: 150,
            sat: 100,
            color_mode: id > 10 ? "COLOR_MODE" : "COLOR_TEMPERATURE_MODE",
            is_active: true,
            name: "what up!" + id
        };
    }
}