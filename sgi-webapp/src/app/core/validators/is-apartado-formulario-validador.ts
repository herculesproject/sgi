import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si es un apartado de un formulario
 */
export class IsApartadoFormulario {
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

  private static checkInterfaceType(apartadoFormulario: any): boolean {
    return apartadoFormulario &&
      apartadoFormulario.hasOwnProperty('id') &&
      apartadoFormulario.hasOwnProperty('bloqueFormulario') &&
      apartadoFormulario.hasOwnProperty('nombre') &&
      apartadoFormulario.hasOwnProperty('apartadoFormularioPadre') &&
      apartadoFormulario.hasOwnProperty('orden') &&
      apartadoFormulario.hasOwnProperty('componenteFormulario') &&
      apartadoFormulario.hasOwnProperty('activo');
  }
}


