import {Component, Input, OnInit} from '@angular/core';
import {Lightbulb} from "../../model/lightbulb";
import {LightbulbService} from "../../service/lightbulb.service";

@Component({
    selector: 'power-control',
    template: require('./powercontrol.component.html'),
    styles: [require('./powercontrol.component.scss')],
})
export class PowerControlComponent implements OnInit{
    @Input() id: number;
    private lightbulb: Lightbulb;

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id)
            .then(bulb => this.lightbulb = bulb);
    }

    powerToggle(): void {
        if(this.lightbulb.power === 'OFF') {
            this.lightbulb.power = 'ON';
        } else {
            this.lightbulb.power = 'OFF';
        }
        this.lightbulbService.sendPowerUpdate(this.lightbulb);
    }
}