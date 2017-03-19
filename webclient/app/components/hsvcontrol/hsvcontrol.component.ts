import {Component, Input, OnInit} from '@angular/core';
import {Lightbulb} from "../../model/lightbulb";
import {LightbulbService} from "../../service/lightbulb.service";

@Component({
    selector: 'hsv-control',
    template: require('./hsvcontrol.component.html'),
    styles: [require('./hsvcontrol.component.scss')],
})
export class HsvControlComponent implements OnInit{
    @Input() id: number;
    private lightbulb: Lightbulb;
    private hueBackground = '-webkit-linear-gradient(left, #FF0000, #FFFF00,#00FF00,#00FFFF,#0000FF,#FF00FF,#FF0000)';
    private SAT_BACKGROUND_TEMPLATE = '-webkit-linear-gradient(left, #888888, hsl($hue,100%,50%))';
    private satBackground = '';
    private brightBackground = '-webkit-linear-gradient(left, #000000, #FFFF00)';

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id)
            .then(bulb => {
                this.lightbulb = bulb;
                this.updateSatBackground();

            });
    }
    updateSatBackground() {
        this.satBackground = this.SAT_BACKGROUND_TEMPLATE.replace(/\$hue/, this.lightbulb.hue + '');
    }

    sendHsvChange(): void {
        this.lightbulbService.sendHsvUpdate(this.lightbulb);
    }

    hueChange(hue: number): void {
        this.lightbulb.hue = hue;
        this.updateSatBackground();
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