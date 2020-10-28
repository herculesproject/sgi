import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si es una entidad
 */
export class IsEntityValidator {
  static isValid(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (typeof control.value === 'string' && control.value.toString().length === 0) {
        return null;
      }
      if (control.value && control.value.id) {
        return null;
      }
      return { invalid: true } as ValidationErrors;
    };
  }
}


