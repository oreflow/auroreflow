import { Injectable } from '@angular/core';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class ApartmentService {
    private LIVINGROOM_LEFT_BULB_ID = '56238300';
    private LIVINGROOM_RIGHT_BULB_ID = '56285014';
    private LIVINGROOM_ROOF_BULB_ID_1 = '56313741';
    private LIVINGROOM_ROOF_BULB_ID_2 = '56279812';
    private BEDROOM_ROOF_BULB_ID_1 = '56276376';
    private BEDROOM_ROOF_BULB_ID_2 = '56312564';
    private KITCHEN_BULB_ID = '56310745';
    private CORRIDOR_BULB_ID_1 = '56232341';

    private LIVINGROOM_BULBS = [
        this.LIVINGROOM_LEFT_BULB_ID,
        this.LIVINGROOM_RIGHT_BULB_ID,
        this.LIVINGROOM_ROOF_BULB_ID_1,
        this.LIVINGROOM_ROOF_BULB_ID_2
    ];
    private BEDROOM_BULBS = [
        this.BEDROOM_ROOF_BULB_ID_1,
        this.BEDROOM_ROOF_BULB_ID_2
    ];
    private KITCHEN_BULBS = [
        this.KITCHEN_BULB_ID
    ];
    private CORRIDOR_BULBS = [
        this.CORRIDOR_BULB_ID_1
    ];

    constructor(){}

    getLivingroomBulbIds() {
        return this.LIVINGROOM_BULBS;
    }
    getBedroomBulbIds() {
        return this.BEDROOM_BULBS;
    }
    getKitchenBulbIds() {
        return this.KITCHEN_BULBS;
    }
    getCorridorBulbIds() {
        return this.CORRIDOR_BULBS;
    }
}