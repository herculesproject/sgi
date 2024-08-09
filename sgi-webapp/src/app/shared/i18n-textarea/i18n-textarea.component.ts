import {
  ChangeDetectionStrategy,
  Component,
  forwardRef,
  Input,
} from '@angular/core';

import { MatFormFieldControl } from '@angular/material/form-field';
import { InputI18nBaseComponent } from '@core/component/input-i18n-base/input-i18n-base.component';

let nextUniqueId = 0;

@Component({
  selector: 'sgi-i18n-textarea',
  templateUrl: './i18n-textarea.component.html',
  styleUrls: ['./i18n-textarea.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: forwardRef(() => I18nTextareaComponent)
    }
  ]
})
export class I18nTextareaComponent extends InputI18nBaseComponent {

  readonly controlType = 'sgi-i18n-textarea';

  /** Unique id for this input. */
  protected uid = this.controlType + `-${nextUniqueId++}`;

  /** Unique id of the element. */
  @Input()
  get id(): string { return this._id; }
  set id(value: string) {
    this._id = value || this.uid;
  }
  // tslint:disable-next-line: variable-name
  private _id: string;

}
