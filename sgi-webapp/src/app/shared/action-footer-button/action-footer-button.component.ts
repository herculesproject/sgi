import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-action-footer-button',
  templateUrl: './action-footer-button.component.html',
  styleUrls: ['./action-footer-button.component.scss']
})
export class ActionFooterButtonComponent {
  @Input() icon = 'save';
  @Input() text: string;
  @Input() color = 'accent';
  @Input() disabled = false;

  constructor(
  ) {

  }
}