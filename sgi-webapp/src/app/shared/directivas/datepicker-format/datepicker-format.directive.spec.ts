import { DatepickerFormatDirective } from './datepicker-format.directive';
import { ElementRef } from '@angular/core';

describe('DatepickerFormatDirective', () => {
  it('should create an instance', () => {
    const directive = new DatepickerFormatDirective(new ElementRef(null));
    expect(directive).toBeTruthy();
  });
});
