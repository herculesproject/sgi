import { AbstractControl, ValidatorFn } from '@angular/forms';

export class StringValidator {

  /**
   * Comprueba que el valor no este incluido en la lista. La comparación es
   * estricta (case-sensitive y accent-sensitive).
   *
   * @param notAllowedValues Lista con los valores que no puede tener.
   * @param getValue Función opcional para extraer el valor del control cuando
   *                 éste no contiene directamente la cadena a comparar.
   */
  static notIn(notAllowedValues: string[], getValue?: (controlValue: any) => string): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      const value = getValue ? getValue(control.value) : control.value;
      if (value && notAllowedValues && notAllowedValues.includes(value)) {
        return { notAllowed: true };
      }
      return null;
    };
  }

  /**
   * Comprueba que el valor no este incluido en la lista ignorando mayúsculas,
   * minúsculas y tildes/acentos.
   *
   * @param notAllowedValues Lista con los valores que no puede tener.
   * @param getValue Función opcional para extraer el valor del control cuando
   *                 éste no contiene directamente la cadena a comparar.
   */
  static notInIgnoreCaseAndAccent(notAllowedValues: string[], getValue?: (controlValue: any) => string): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      const value = (getValue ? getValue(control.value) : control.value)?.trim();
      if (!value || !notAllowedValues) {
        return null;
      }
      const isDuplicated = notAllowedValues
        .some(notAllowed => notAllowed?.trim()
          && value.localeCompare(notAllowed.trim(), undefined, { sensitivity: 'base' }) === 0);
      return isDuplicated ? { notAllowed: true } : null;
    };
  }

}
