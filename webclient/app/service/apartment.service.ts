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
    public static LIVINGROOM_LEFT_BULB_ID = '56238300';
    public static LIVINGROOM_RIGHT_BULB_ID = '56285014';
    public static LIVINGROOM_ROOF_BULB_ID_1 = '56313741';
    public static LIVINGROOM_ROOF_BULB_ID_2 = '56279812';
    public static BEDROOM_ROOF_BULB_ID_1 = '56276376';
    public static BEDROOM_ROOF_BULB_ID_2 = '56312564';
    public static KITCHEN_BULB_ID = '56310745';
    public static HALLWAY_BULB_ID = '56232341';
    public static ENTRANCE_BULB_ID = '56310674';
    public static BATHROOM_BULB_ID = '56229429';
}