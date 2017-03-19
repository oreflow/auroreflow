import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
    selector: 'toggle-switch',
    template: require('./toggleswitch.component.html'),
    styles: [require('./toggleswitch.component.scss')],
})
export class ToggleSwitchComponent {

    @Input() toggled: boolean;
    @Input() toggleDisabled: boolean;
    @Output() onToggled = new EventEmitter<boolean>();

    statusChange() {
        this.toggled = !this.toggled;
        this.onToggled.emit(this.toggled);
    }
}