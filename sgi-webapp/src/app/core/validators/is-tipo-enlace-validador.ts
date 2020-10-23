import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Validador para comprobar si es convocatoria enlace
 */
export class IsTipoEnlace {
  static isValid(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value || typeof control.value === 'string' && control.value.toString().length === 0) {
        return null;
      }
      if (this.checkInterfaceType(control.value)) {
        return null;
      }
      return { invalid: true };
    };
  }

  private static checkInterfaceType(convocatoriaEnlace: any): boolean {
    return convocatoriaEnlace &&
      convocatoriaEnlace.hasOwnProperty('id') &&
      convocatoriaEnlace.hasOwnProperty('nombre') &&
      convocatoriaEnlace.hasOwnProperty('descripcion') &&
      convocatoriaEnlace.hasOwnProperty('activo');
  }
}


