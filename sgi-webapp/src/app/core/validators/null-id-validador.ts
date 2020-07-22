import { AbstractControl, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar una hora
 */
export class NullIdValidador {
  isValid(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (!control.value.id) {
        return { vacio: true };
      }
      return null;
    };
  }
}
