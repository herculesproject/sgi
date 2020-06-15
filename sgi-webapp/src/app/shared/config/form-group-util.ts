import { FormControl, FormGroup, ValidatorFn } from '@angular/forms';

/**
 * Clase para manejar los formGroups de los formularios
 */
export class FormGroupUtil {
  /**
   * Comprueba que todos los datos de un formGroup son válidos.
   * Si nos ha fallos devuelve true.
   * En caso contrario, marca todos los campos con errores y devuelve false.
   *
   * @param formGroup FormGroup
   */
  static validFormGroup(formGroup: FormGroup): boolean {
    let result = true;
    const list: string[] = Object.keys(formGroup.controls);
    list.forEach((element) => {
      if (formGroup.get(element).errors != null) {
        formGroup.get(element).markAllAsTouched();
        result = false;
      }
    });
    return result;
  }

  /**
   * Comprueba si un dato de un formGroup es inválido y
   * ha sido modificado por el usuario.
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   */
  static checkError(formGroup: FormGroup, key: string): boolean {
    if (formGroup.get(key)) {
      return (
        formGroup.get(key).invalid &&
        (formGroup.get(key).dirty || formGroup.get(key).touched)
      );
    }
    return false;
  }

  /**
   * Devuelve los errores de un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   */
  static getError(formGroup: FormGroup, key: string) {
    return formGroup.get(key).errors;
  }

  /**
   * Devuelve el valor de un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   */
  static getValue(formGroup: FormGroup, key: string) {
    return formGroup.get(key).value;
  }

  /**
   * Cambia el valor de un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param value Nuevo valor
   */
  static setValue(formGroup: FormGroup, key: string, value: any) {
    return formGroup.get(key).setValue(value);
  }

  /**
   * Añade un validador a un dato concreto de un formGroup
   *
   * @param formGroup FormGroup a comprobar
   * @param key Identificador del dato
   * @param validator Nuevo validador
   */
  static addValidator(
    formGroup: FormGroup,
    key: string,
    validator: ValidatorFn[]
  ) {
    return formGroup.setControl(key, new FormControl('', validator));
  }

  /**
   * Añade un nuevo dato a un formGRoup
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
    formGroup.addControl(key, formControl);
  }
}
