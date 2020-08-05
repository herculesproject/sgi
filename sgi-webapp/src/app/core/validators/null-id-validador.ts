import { AbstractControl, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si tiene relleno el id
 */
export class NullIdValidador {
  isValid(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (!control.value || !control.value.id) {
        return { vacio: true };
      }
      return null;
    };
  }
}
