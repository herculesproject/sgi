import { Component, Input } from '@angular/core';

/**
 * Icono de ayuda de un campo con un tooltip de contenido HTML. Si no hay contenido no se muestra.
 */
@Component({
  selector: 'sgi-field-info-html',
  templateUrl: './field-info-html.component.html',
  styleUrls: ['./field-info-html.component.scss']
})
export class FieldInfoHtmlComponent {

  /** Contenido HTML de la ayuda */
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
