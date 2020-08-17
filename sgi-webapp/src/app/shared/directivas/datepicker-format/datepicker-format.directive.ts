import { Directive, HostBinding, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appDatepickerFormat]'
})
export class DatepickerFormatDirective {

  /**
   * Crea el formato fecha si metemos números
   * en el campo de texto
   */

  @HostBinding('class.my-focused-element') isFocused: boolean;
  private el: HTMLInputElement;
  patternNumber = /^(\d{2})\/(\d{2})\/(\d{4})$/;

  constructor(
    private elementRef: ElementRef
  ) {
    this.el = this.elementRef.nativeElement;

  }

  @HostListener('blur', ['$event.target.value'])
  onBlur(value) {
    const regExp = new RegExp(this.patternNumber);
    if (regExp.test(value)) {
      this.el.value = value;
    }

  }

}

