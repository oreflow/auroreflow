import { Component, OnInit } from '@angular/core';

import { LightbulbService } from '../../service/lightbulb.service';
import {ApartmentService} from "../../service/apartment.service";

@Component({
  template: require('./mapview.component.html'),
  styles: [require('./mapview.component.scss')],
})
export class MapViewComponent implements OnInit{

    private ON_CT = 3000;
    private ON_BRIGHTNESS = 40;
    private OFF_POWER = 'OFF';

    private LIVINGROOM_BULB_IDS: string[];
    private BEDROOM_BULB_IDS: string[];
    private KITCHEN_BULB_IDS: string[];
    private CORRIDOR_BULB_IDS: string[];

    private LIVINGROOM_ON: boolean;
    private BEDROOM_ON: boolean;
    private KITCHEN_ON: boolean;
    private CORRIDOR_ON: boolean;

    constructor(private lightbulbService: LightbulbService,
                private apartmentService: ApartmentService) {
        this.LIVINGROOM_BULB_IDS = apartmentService.getLivingroomBulbIds();
        this.BEDROOM_BULB_IDS = apartmentService.getBedroomBulbIds();
        this.KITCHEN_BULB_IDS = apartmentService.getKitchenBulbIds();
        this.CORRIDOR_BULB_IDS = apartmentService.getCorridorBulbIds();
        this.lightbulbService.getInitPromise().then(() => {
            const lightbulbs = this.lightbulbService.getListofLightbulbs();
            if(lightbulbs.length > 0) {
                this.LIVINGROOM_ON = !!lightbulbs
                    .find(lightbulb => this.LIVINGROOM_BULB_IDS.indexOf(lightbulb.id) >= 0
                    && lightbulb.power === 'ON');
                this.BEDROOM_ON = !!lightbulbs
                    .find(lightbulb => this.BEDROOM_BULB_IDS.indexOf(lightbulb.id) >= 0
                    && lightbulb.power === 'ON');
                this.KITCHEN_ON = !!lightbulbs
                    .find(lightbulb => this.KITCHEN_BULB_IDS.indexOf(lightbulb.id) >= 0
                    && lightbulb.power === 'ON');
                this.CORRIDOR_ON = !!lightbulbs
                    .find(lightbulb => this.CORRIDOR_BULB_IDS.indexOf(lightbulb.id) >= 0
                    && lightbulb.power === 'ON');
            }
        });
    }

    ngOnInit(): void {
    }

    toggleLivingRoom() {
        this.LIVINGROOM_ON = !this.LIVINGROOM_ON;
        this.sendUpdatesTo(this.LIVINGROOM_BULB_IDS, this.LIVINGROOM_ON);
    }
    toggleBedRoom() {
        this.BEDROOM_ON = !this.BEDROOM_ON;
        this.sendUpdatesTo(this.BEDROOM_BULB_IDS, this.BEDROOM_ON);
    }
    toggleKitchen() {
        this.KITCHEN_ON = !this.KITCHEN_ON;
        this.sendUpdatesTo(this.KITCHEN_BULB_IDS, this.KITCHEN_ON);
    }
    toggleCorridor() {
        this.CORRIDOR_ON = !this.CORRIDOR_ON;
        this.sendUpdatesTo(this.CORRIDOR_BULB_IDS, this.CORRIDOR_ON);
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