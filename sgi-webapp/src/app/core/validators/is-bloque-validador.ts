import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si es un bloque de un formulario
 */
export class IsBloque {
  static isValid(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (typeof control.value === 'string' && control.value.toString().length === 0) {
        return null;
      }
      if (this.checkInterfaceType(control.value)) {
        return null;
      }
      return { invalid: true };
    };
  }

  private static checkInterfaceType(bloque: any): boolean {
    return bloque &&
      bloque.hasOwnProperty('id') &&
      bloque.hasOwnProperty('formulario') &&
      bloque.hasOwnProperty('nombre') &&
      bloque.hasOwnProperty('orden');
  }
}


