import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { I18nFieldValue } from '@core/i18n/i18n-field';

export class I18nValidators {

  static required: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }

    if (control.value instanceof Array && control.value.length > 0) {
      var data = control.value as I18nFieldValue[];
      for (const element of data) {
        if (element.value && element.value.length > 0) {
          return null;
        }
      }
    }

    return { required: true } as ValidationErrors;
  };

  static maxLength(maxLength: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }

      if (control.value instanceof Array && control.value.length > 0) {
        var data = control.value as I18nFieldValue[];
        for (const element of data) {
          if (element.value && element.value.length > maxLength) {
            return { maxlength: true } as ValidationErrors;
          }
        }
      }

      return null;
    };
  }

}
