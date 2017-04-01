import {Component, Input, OnInit, OnChanges, SimpleChanges} from '@angular/core';
import {Lightbulb} from "../../model/lightbulb";
import {LightbulbService} from "../../service/lightbulb.service";

@Component({
    selector: 'hsv-control',
    template: require('./hsvcontrol.component.html'),
    styles: [require('./hsvcontrol.component.scss')],
})
export class HsvControlComponent implements OnInit, OnChanges {
    ngOnChanges(changes: SimpleChanges): void {
        console.log('CHANGES!!!');
    }
    @Input() id: string;
    private lightbulb: Lightbulb;
    private HUE_REGEX = /\$hue/g;
    private SAT_REGEX = /\$sat/g;
    private BRIGHT_REGEX = /\$bright/g;
    private HUE_BACKGROUND = '-webkit-linear-gradient(left, #FF0000, #FFFF00,#00FF00,#00FFFF,#0000FF,#FF00FF,#FF0000)';


    private SAT_FRAG_1 = '-webkit-linear-gradient(left, hsl(';
    private SAT_FRAG_2 = ',0%, 50%), hsl(';
    private SAT_FRAG_3 = ',100%, 50%))';
    private satBackground = '';
    private BRIGHT_BACKGROUND = '-webkit-linear-gradient(left, #555555, #FFFFFF)';

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id)
            .then(bulb => {
                this.lightbulb = bulb;
            });
    }

    sendHsvChange(): void {
        this.lightbulbService.sendHsvUpdate(this.lightbulb);
    }

    hueChange(hue: number): void {
        this.lightbulb.hue = hue;
        this.sendHsvChange();
    }

    satChange(sat: number): void {
        this.lightbulb.sat = sat;
        this.sendHsvChange();
    }

    brightnessChange(brightness: number): void {
        this.lightbulb.bright = brightness;
        this.sendHsvChange();
    }
}