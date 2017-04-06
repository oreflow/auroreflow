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