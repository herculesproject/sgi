import {
  Component,
  forwardRef
} from '@angular/core';

import { MatFormFieldControl } from '@angular/material/form-field';
import { InputI18nBaseComponent } from '@core/component/input-i18n-base/input-i18n-base.component';

let nextUniqueId = 0;

@Component({
  selector: 'sgi-i18n-textarea',
  templateUrl: './i18n-textarea.component.html',
  styleUrls: ['./i18n-textarea.component.scss'],
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: forwardRef(() => I18nTextareaComponent)
    }
  ]
})
export class I18nTextareaComponent extends InputI18nBaseComponent {

  private readonly controlType = 'sgi-i18n-textarea';

  /** Unique id for this input. */
  protected readonly uid = this.controlType + `-${nextUniqueId++}`;

}
