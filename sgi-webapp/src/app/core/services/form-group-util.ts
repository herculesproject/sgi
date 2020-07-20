import {
  AbstractControl,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn
} from '@angular/forms';
import {Subscription} from 'rxjs';

/**
 * Clase para manejar los formGroups de los formularios
 */
export class FormGroupUtil {
  /**
   * Comprueba que todos los datos de un formGroup son válidos.
   * Si nos ha fallos devuelve true.
   * En caso contrario, marca todos los campos con errores y devuelve false.
   *
   * @param formGroup FormGroup a comprobar
   */
  static validFormGroup(formGroup: FormGroup): boolean {
    if (formGroup) {
      let result = true;
      const list: string[] = Object.keys(formGroup.controls);
      list.forEach((key: string) => {
        const abstractControl: AbstractControl = formGroup.get(key);
        if (this.getError(formGroup, key) != null) {
          abstractControl.markAllAsTouched();
          result = false;
        }
      });
      return result;
    }
    return false;
  }

  /**
   * Devuelve el número de errores que contiene un formGroup
   *
   * @param formGroup FormGroup a comprobar
   */
  static getNumErrors(formGroup: FormGroup): number {
    let errors = 0;
    if (formGroup) {
      const list: string[] = Object.keys(formGroup.controls);
      list.forEach((key: string) => {
        const abstractControl: AbstractControl = formGroup.get(key);
        if (this.getError(formGroup, key) != null) {
          errors++;
          abstractControl.markAllAsTouched();
        }
      });
    }
    return errors;
  }

  /**
   * Comprueba si un dato de un formGroup es inválido y
   * ha sido modificado por el usuario.
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   */
  static checkError(formGroup: FormGroup, key: string): boolean {
    if (formGroup) {
      const abstractControl: AbstractControl = formGroup.get(key);
      if (abstractControl) {
        return (
          abstractControl.invalid &&
          (abstractControl.dirty || abstractControl.touched)
        );
      }
    }
    return false;
  }

  /**
   * Devuelve los errores de un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   */
  static getError(formGroup: FormGroup, key: string): ValidationErrors {
    if (formGroup) {
      const abstractControl: AbstractControl = formGroup.get(key);
      if (abstractControl) {
        return abstractControl.errors;
      }
    }
    return null;
  }

  /**
   * Devuelve el valor de un dato concreto de un formGroup.
   * Si no existe el dato devuelve null
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   */
  static getValue(formGroup: FormGroup, key: string) {
    if (formGroup) {
      const abstractControl: AbstractControl = formGroup.get(key);
      if (abstractControl) {
        return abstractControl.value;
      }
    }
    return null;
  }

  /**
   * Cambia el valor de un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param value Nuevo valor
   */
  static setValue(formGroup: FormGroup, key: string, value: any): void {
    if (formGroup) {
      const abstractControl: AbstractControl = formGroup.get(key);
      if (abstractControl) {
        abstractControl.setValue(value);
      }
    }
  }

  /**
   * Cambia los validadores a un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param validator Lista de validadores
   * @param initValue Valor inicial del dato
   */
  static changeValidator(
    formGroup: FormGroup,
    key: string,
    validator: ValidatorFn[],
    initValue?: any
  ): void {
    if (formGroup) {
      const abstractControl: AbstractControl = formGroup.get(key);
      if (abstractControl) {
        formGroup.setControl(key, new FormControl(initValue, validator));
      }
    }
  }

  /**
   * Añade un nuevo dato a un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param formControl Nuevo valor
   */
  static addFormControl(
    formGroup: FormGroup,
    key: string,
    formControl: FormControl
  ): void {
    if (formGroup && formControl) {
      formGroup.addControl(key, formControl);
    }
  }

  /**
   * Crea una subcripción a un dato del formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param fun Función que se ejecuta cada vez que cambia el dato
   */
  static subscribeOneValue(
    formGroup: FormGroup, key: string, fun: (res?) => void): Subscription {
    if (formGroup) {
      const abstractControl: AbstractControl = formGroup.get(key);
      if (abstractControl) {
        return abstractControl.valueChanges.subscribe(value => {
          fun(value);
        });
      }
    }
    return null;
  }

  /**
   * Crea una subcripción a todos los datos del formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param fun Función que se ejecuta cada vez que cambia el dato al que esta
   * subcrito
   */
  static subscribeValues(formGroup: FormGroup, fun: (res?) => void): Subscription[] {
    if (formGroup) {
      const result: Subscription[] = [];
      const list: string[] = Object.keys(formGroup.controls);
      list.forEach((key: string) => {
        result.push(this.subscribeOneValue(formGroup, key, fun));
      });
      return result;
    }
    return null;
  }
}
