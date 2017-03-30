import { Injectable } from '@angular/core';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

import {LightbulbService} from "./lightbulb.service";
import {Lightbulb} from "../model/lightbulb";

@Injectable()
export class WebsocketService {

    private lightbulbUpdateSocket: WebSocket;

    constructor(private lightbulbService: LightbulbService) {
        this.lightbulbUpdateSocket = new WebSocket("ws://" + window.location.host + "/lightbulbupdates");
        this.lightbulbUpdateSocket.onmessage = (event) => { this.handleLightbulbUpdate(event); };
    }

    handleLightbulbUpdate(event: any) {
        const updatedLightbulb = JSON.parse(event.data) as Lightbulb;
        this.lightbulbService.putLightbulb(updatedLightbulb);
    }
}