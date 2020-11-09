import { ValidatorFn, FormGroup, ValidationErrors } from '@angular/forms';

export class NumberValidator {

  /**
   * Comprueba que el segundo numero es posterior al primero.
   *
   * @param firstNumberFieldName Nombre del campo contra el que se quiere hacer la validacion.
   * @param secondNumberFieldName Nombre del campo que se quiere validar.
   */
  static isAfer(firstNumberFieldName: string, secondNumberFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const numeroAnteriorControl = formGroup.controls[firstNumberFieldName];
      const numeroPosteriorControl = formGroup.controls[secondNumberFieldName];

      if (numeroPosteriorControl.errors && !numeroPosteriorControl.errors.after) {
        return;
      }

      const numeroAnteriorNumber = numeroAnteriorControl.value;
      const numeroPosteriorNumber = numeroPosteriorControl.value;

      if (numeroPosteriorNumber && (!numeroAnteriorNumber || numeroAnteriorNumber >= numeroPosteriorNumber)) {
        numeroPosteriorControl.setErrors({ after: true });
        numeroPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (numeroPosteriorControl.errors) {
        delete numeroPosteriorControl.errors.after;
        numeroPosteriorControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

}
