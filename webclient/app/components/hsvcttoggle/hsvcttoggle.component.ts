import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Lightbulb} from "../../model/lightbulb";
import {LightbulbService} from "../../service/lightbulb.service";

@Component({
    selector: 'hsv-ct-toggle',
    template: require('./hsvcttoggle.component.html'),
    styles: [require('./hsvcttoggle.component.scss')],
})
export class HsvCtToggleComponent implements OnInit {

    @Input() id: string;
    private lightbulb: Lightbulb;

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id).then(bulb => this.lightbulb = bulb);
    }

    toggleHandler(toggled: boolean) {
        if(toggled) {
            this.lightbulb.colorMode = 'COLOR_MODE';
            this.lightbulbService.sendHsvUpdate(this.lightbulb);
        } else {
            this.lightbulb.colorMode = 'COLOR_TEMPERATURE_MODE';
            this.lightbulbService.sendCtUpdate(this.lightbulb);
        }
    }
}