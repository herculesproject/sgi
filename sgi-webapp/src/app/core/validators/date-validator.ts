import { ValidatorFn, FormGroup, ValidationErrors, AbstractControl } from '@angular/forms';
import { DateUtils } from '@core/utils/date-utils';
import { Fragment } from '@core/services/action-service';

export class DateValidator {

  /**
   * Comprueba que la segunda fecha es posterior a la primera.
   *
   * @param firstDateFieldName Nombre del campo contra el que se quiere hacer la validacion.
   * @param secondDateFieldName Nombre del campo que se quiere validar.
   * @param errorIfFirstDateEmpty Mostrar error si la primera fecha no esta rellena (por defecto true).
   */
  static isAfter(firstDateFieldName: string, secondDateFieldName: string, errorIfFirstDateEmpty = true): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaAnteriorControl = formGroup.controls[firstDateFieldName];
      const fechaPosteriorControl = formGroup.controls[secondDateFieldName];

      if (fechaPosteriorControl.errors && !fechaPosteriorControl.errors.after) {
        return;
      }

      const fechaAnteriorDate = DateUtils.fechaToDate(fechaAnteriorControl.value);
      const fechaPosteriorDate = DateUtils.fechaToDate(fechaPosteriorControl.value);

      if (fechaPosteriorDate && ((!fechaAnteriorDate && errorIfFirstDateEmpty)
        || (fechaAnteriorDate && fechaAnteriorDate >= fechaPosteriorDate))) {
        fechaPosteriorControl.setErrors({ after: true });
        fechaPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (fechaPosteriorControl.errors) {
        delete fechaPosteriorControl.errors.after;
        fechaPosteriorControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  /**
   * Comprueba que la segunda fecha es posterior o igual a la primera.
   *
   * @param firstDateFieldName Nombre del campo contra el que se quiere hacer la validacion.
   * @param secondDateFieldName Nombre del campo que se quiere validar.
   */
  static isAfterOrEqual(firstDateFieldName: string, secondDateFieldName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaAnteriorControl = formGroup.controls[firstDateFieldName];
      const fechaPosteriorControl = formGroup.controls[secondDateFieldName];

      if (fechaPosteriorControl.errors && !fechaPosteriorControl.errors.after) {
        return;
      }

      const fechaAnteriorDate = DateUtils.fechaToDate(fechaAnteriorControl.value);
      const fechaPosteriorDate = DateUtils.fechaToDate(fechaPosteriorControl.value);


      if (fechaPosteriorDate && (fechaAnteriorDate > fechaPosteriorDate)) {
        fechaPosteriorControl.setErrors({ after: true });
        fechaPosteriorControl.markAsTouched({ onlySelf: true });
      } else if (fechaPosteriorControl.errors) {
        delete fechaPosteriorControl.errors.after;
        fechaPosteriorControl.updateValueAndValidity({ onlySelf: true });
      }
    };
  }

  static isBefore(fechaPosterior: string, fechaAnterior: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {

      const fechaAnteriorControl = formGroup.controls[fechaAnterior];
      const fechaPosteriorControl = formGroup.controls[fechaPosterior];

      if (fechaAnteriorControl.errors && !fechaAnteriorControl.errors.before) {
        return;
      }

      const fechaAnteriorDate = DateUtils.fechaToDate(fechaAnteriorControl.value);
      const fechaPosteriorDate = DateUtils.fechaToDate(fechaPosteriorControl.value);


      if (fechaAnteriorDate && (fechaPosteriorDate < fechaAnteriorDate)) {
        fechaAnteriorControl.setErrors({ before: true });
      } else {
        fechaAnteriorControl.setErrors(null);
      }
    };
  }

  static maxDate(maxDate: Date): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const date = new Date(control.value);
      if (date > maxDate) {
        return { maxDate: true };
      }
      return null;
    };
  }

  static minDate(minDate: Date): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const date = new Date(control.value);
      if (date < minDate) {
        return { minDate: true };
      }
      return null;
    };
  }

  static rangeWithTime(controlName: string, beforeControlName: string, afterControlName: string): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors | null => {
      const control = formGroup.controls[controlName];
      if (control.errors) {
        delete control.errors.rangeWithTime;
        if (Object.keys(control.errors).length === 0) {
          control.setErrors(null);
        }
      }
      const beforeControl = formGroup.controls[beforeControlName];
      const afterControl = formGroup.controls[afterControlName];
      if (beforeControl.value && afterControl.value && control.value) {
        const date = new Date(control.value);
        const before = new Date(beforeControl.value);
        before.setHours(0);
        before.setMinutes(0);
        before.setSeconds(0);
        const after = new Date(afterControl.value);
        after.setHours(23);
        after.setMinutes(59);
        after.setSeconds(59);
        if (after >= date && date >= before) {
          return;
        }
        control.setErrors({ rangeWithTime: true });
        control.markAsTouched({ onlySelf: true });
        return;
      }
    };
  }
}
