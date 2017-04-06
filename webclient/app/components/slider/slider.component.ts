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
import {
    Component, Input, Output, EventEmitter, ViewChild, ElementRef, OnChanges
} from '@angular/core';

@Component({
    selector: 'slider',
    template: require('./slider.component.html'),
    styles: [require('./slider.component.scss')],
})
export class SliderComponent implements OnChanges {
    @Input() background: string;
    @Input() value: number;
    @Input() minValue: number;
    @Input() maxValue: number;

    @Output() onValueChange = new EventEmitter<number>();

    @ViewChild('sliderBar') sliderElement: ElementRef;

    sliderOffset: string;
    ngOnChanges(): void {
        let sliderElementWidth = ((this.sliderElement as any).nativeElement as HTMLDivElement).clientWidth;
        let intervalSize = (this.maxValue - this.minValue + 1);
        this.sliderOffset = ((this.value - this.minValue) / intervalSize) * sliderElementWidth + 'px';
    }

    sliderMouseEvent(event:MouseEvent) {
        if(event.buttons === 1 && event.button === 0) {
            let eventClientXOffset = event.clientX;
            this.handleTouch(eventClientXOffset)
        }
    }
    sliderTouchEvent(event:TouchEvent) {
        let eventClientXOffset = event.targetTouches.item(0).clientX;
        this.handleTouch(eventClientXOffset)
    }


    handleTouch(eventClientXOffset: number) {
        let elementClientRect = ((this.sliderElement as any).nativeElement as HTMLDivElement).getClientRects()[0];
        let sliderElementOffset = elementClientRect.left;
        let sliderElementWidth = ((this.sliderElement as any).nativeElement as HTMLDivElement).clientWidth;

        let trimmedEventOffset = eventClientXOffset;
        trimmedEventOffset = Math.max(trimmedEventOffset, sliderElementOffset);
        trimmedEventOffset = Math.min(trimmedEventOffset, sliderElementOffset + sliderElementWidth);

        this.sliderOffset = trimmedEventOffset - sliderElementOffset - 5 + 'px';
        this.emitValue((trimmedEventOffset - sliderElementOffset) / sliderElementWidth);
    }

    emitValue(sliderOffsetPercent: number) {
        let newValue = Math.round((this.maxValue - this.minValue) * sliderOffsetPercent + 1) + this.minValue;
        this.onValueChange.emit(newValue);
    }
}