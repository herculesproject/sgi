import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si es un bloque de un formulario
 */
export class IsBloqueFormulario {
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

  private static checkInterfaceType(bloqueFormulario: any): boolean {
    return bloqueFormulario &&
      bloqueFormulario.hasOwnProperty('id') &&
      bloqueFormulario.hasOwnProperty('formulario') &&
      bloqueFormulario.hasOwnProperty('nombre') &&
      bloqueFormulario.hasOwnProperty('orden') &&
      bloqueFormulario.hasOwnProperty('activo');
  }
}


