import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Lightbulb} from "../../model/lightbulb";
import {LightbulbService} from "../../service/lightbulb.service";

@Component({
    selector: 'editable-name',
    template: require('./editablename.component.html'),
    styles: [require('./editablename.component.scss')],
})
export class EditableNameComponent implements OnInit {

    @Input() id: string;
    private lightbulb: Lightbulb;

    constructor(private lightbulbService: LightbulbService) {}

    ngOnInit(): void {
        this.lightbulbService.getLightbulb(this.id).then(bulb => this.lightbulb = bulb);
    }

    updateName() {
        this.lightbulbService.sendNameUpdate(this.lightbulb);
    }
}