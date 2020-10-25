import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si es un apartado de un formulario
 */
export class IsApartado {
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

  private static checkInterfaceType(apartado: any): boolean {
    return apartado &&
      apartado.hasOwnProperty('id') &&
      apartado.hasOwnProperty('bloque') &&
      apartado.hasOwnProperty('nombre') &&
      apartado.hasOwnProperty('padre') &&
      apartado.hasOwnProperty('orden');
  }
}


