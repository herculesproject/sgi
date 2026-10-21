import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-info-message',
  templateUrl: './info-message.component.html',
  styleUrls: ['./info-message.component.scss']
})
export class InfoMessageComponent {

  @Input()
  get message(): string {
    return this._message;
  }
  set message(value: string) {
    this._message = value;
  }
  // tslint:disable-next-line: variable-name
  private _message = '';

  /** Contenido HTML del mensaje. Si se indica, se muestra en lugar de message */
  @Input()
  get html(): string {
    return this._html;
  }
  set html(value: string) {
    this._html = value;
  }
  // tslint:disable-next-line: variable-name
  private _html = '';

  constructor() { }
}
