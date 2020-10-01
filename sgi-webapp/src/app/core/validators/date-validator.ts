import { ValidatorFn, FormGroup, ValidationErrors } from '@angular/forms';
import { DateUtils } from '@core/utils/date-utils';
/**
 * Comprueba que la primera fecha es anterior a la segunda.
 *
 * @param fechaPosterior Nombre del campo que se quiere validar.
 * @param fechaAnterior Nombre del campo contra el que se quiere hacer la validacion.
 */
export class DateValidator {
  isAfter(fechaAnterior: string, fechaPosterior: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaAnteriorControl = formGroup.controls[fechaAnterior];
      const fechaPosteriorControl = formGroup.controls[fechaPosterior];

      if (fechaPosteriorControl.errors && !fechaPosteriorControl.errors.after) {
        return;
      }

      const fechaAnteriorDate = DateUtils.fechaToDate(fechaAnteriorControl.value);
      const fechaPosteriorDate = DateUtils.fechaToDate(fechaPosteriorControl.value);


      if (fechaPosteriorDate && (!fechaAnteriorDate || fechaAnteriorDate > fechaPosteriorDate)) {
        fechaPosteriorControl.setErrors({ after: true });
      } else {
        fechaPosteriorControl.setErrors(null);
      }
    };
  }
}
