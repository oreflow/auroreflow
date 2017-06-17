import { Component, OnInit } from '@angular/core';
import { LightbulbService } from './service/lightbulb.service';
import {WebsocketService} from "./service/websocket.service";

@Component({
  selector: 'auroreflow-app',
  template: require('./app.component.html'),
  styles: [require('./app.component.scss')],
})
export class AppComponent implements OnInit{
    constructor(private lightbulbService: LightbulbService,
                private websocketService: WebsocketService) {}
    ngOnInit(): void {
        window.onfocus = () => {
            console.log('reiniting')
            this.websocketService.connectToSocket();
            this.lightbulbService.loadLightbulbList();
        };
    }

    powerOffAll() {
        this.lightbulbService.sendPowerOffAll();
    }
}