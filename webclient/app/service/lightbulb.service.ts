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
    private lightbulbs: Lightbulb[] = [];
    private initPromise : Promise<any>;

    constructor(private http: Http) {
        this.initPromise = this.loadLightbulbList();
    }

    /**
     * Triggers a reaload of the list of lightbulbs. Returns a promise containing no data.
     * Accessing the list of Lightbulbs should always be done using the getListOfLightbulbs
     * Method
     */
    loadLightbulbList(): Promise<void> {
        return this.http
            .get('lightbulb/list')
            .map((res: Response) => res.json() as Lightbulb[])
            .toPromise()
            .then(lightbulbList => lightbulbList.forEach(lightbulb => this.putLightbulb(lightbulb)))
            .catch((err) => {
                console.log(err);
                this.putLightbulb(this.createMockLightbulb(5));

                setTimeout(() => {
                    this.putLightbulb(this.createMockLightbulb(27));
                },2000);

                this.runMockLightbulbUpdates();
            })
            .then(() => null);
    }

    /**
     * Gets the initialization promise
     */
    getInitPromise(): Promise<void> {
        return this.initPromise;
    };

    /**
     * Gets a list of currently active lightbulbs
     */
    getListofLightbulbs(): Lightbulb[] {
        return this.lightbulbs;
    };

    /**
     * Checks if a given LightbulbId exists
     */
    hasLightbulb(id: string): boolean {
        return !!this.lightbulbs.find(lightbulb => lightbulb.id ==id);
    }

    /**
     * Gets a specific lightbulb by ID
     */
    getLightbulb(id: string): Promise<Lightbulb> {
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
     * Sends a Request to change the name the name set in provided Lightbulb object
     */
    sendNameRequest(lightbulb: Lightbulb): void {
        this.sendUpdateRequest(lightbulb,
            {
                nameRequest: {
                    name: lightbulb.name
                },
            });
    }

    /**
     * Sends a Hsv update request using the HSV values provided in the Lightbulb object
     */
    sendHsvUpdate(lightbulb: Lightbulb): void {
        lightbulb.power = "ON";
        this.sendUpdateRequest(lightbulb,
            {
                hsvRequest: {
                    hue: lightbulb.hue,
                    sat: lightbulb.sat,
                    brightness: lightbulb.bright,
                },
            });
    }
    /**
     * Sends a Ct update request using the Ct values provided in the Lightbulb object
     */
    sendCtUpdate(lightbulb: Lightbulb): void {
        lightbulb.power = "ON";
        this.sendUpdateRequest(lightbulb,
            {
                ctRequest: {
                    ct: lightbulb.ct,
                    brightness: lightbulb.bright,
                },
            });
    }
    /**
     * Sends a Power update request using the power value provided in the Lightbulb object
     */
    sendPowerUpdate(lightbulb: Lightbulb): void {
        this.sendUpdateRequest(lightbulb,
            {
                powerRequest: {
                    power: lightbulb.power
                },
            });
    }
    /**
     * Sends a Name update request using the name value provided in the Lightbulb object
     */
    sendNameUpdate(lightbulb: Lightbulb): void {
        this.sendUpdateRequest(lightbulb,
            {
                nameRequest: {
                    name: lightbulb.name
                },
            });
    }

    /**
     * Sends a provided LightbulbRequest to the API
     */
    private sendUpdateRequest(lightbulb: Lightbulb, request: LightbulbRequest): void {
        let requestTime = new Date().getTime();
        lightbulb.lastChangeMillis = "" + requestTime;
        request.requestTime = requestTime;
        this.http
            .put('lightbulb/update/' + lightbulb.id,
                JSON.stringify(request),
                { headers: this.PUT_HEADERS })
            .map((res: Response) => res.json().data as Lightbulb[])
            .toPromise();
    }

    /**
     * Sends a power off all bulbs request
     */
    sendPowerOffAll(): void {
        this.http.get('poweroff').toPromise();
    }


    /**
     * Puts a lightbulb into the list of lightbulbs, replaces existing lightbulb if it already exists
     */
    putLightbulb(lightbulb: Lightbulb) {
        const existingIndex = this.lightbulbs.findIndex(bulb => bulb.id === lightbulb.id);
        if(existingIndex >= 0) {
            const existingLightbulb = this.lightbulbs[existingIndex];
            if(!existingLightbulb.lastChangeMillis
                || existingLightbulb.lastChangeMillis < lightbulb.lastChangeMillis) {
                console.log("Updating lightbulb", lightbulb)
                existingLightbulb.power = lightbulb.power;
                existingLightbulb.bright = lightbulb.bright;
                existingLightbulb.ct = lightbulb.ct;
                existingLightbulb.hue = lightbulb.hue;
                existingLightbulb.sat = lightbulb.sat;
                existingLightbulb.colorMode = lightbulb.colorMode;
                existingLightbulb.isActive = lightbulb.isActive;
                existingLightbulb.name = lightbulb.name;
                existingLightbulb.lastChangeMillis = lightbulb.lastChangeMillis;
            }
        } else {
            this.lightbulbs.push(lightbulb);
        }
        // This is really inefficient, but so be it for now
        this.lightbulbs = this.lightbulbs
            .sort((bulbA, bulbB) => { return bulbA.name.localeCompare(bulbB.name);});
    }


    createMockLightbulb(id: number): Lightbulb {
        console.log('creating bulb with ID', id);
        return {
            id: "" + id,
            model: id > 10 ? "COLOR" : "MONO",
            location: "yeelight://192.168.0.179:55443",
            ip: "192.168.0.179",
            port: 55443,
            power: "ON",
            bright: Math.floor(Math.random()* 100 + 1),
            ct: Math.floor(Math.random()*3500) + 1700,
            hue: Math.floor(Math.random()*360),
            sat: 100,
            colorMode: id > 10 ? "COLOR_MODE" : "COLOR_TEMPERATURE_MODE",
            isActive: true,
            name: "what up!" + id,
            lastChangeMillis: new Date().getTime() + ""
        };
    }

    runMockLightbulbUpdates() {
        setTimeout(() => {
            this.putLightbulb(this.createMockLightbulb(18));
            this.runMockLightbulbUpdates();
            }, 5000);
    }

}