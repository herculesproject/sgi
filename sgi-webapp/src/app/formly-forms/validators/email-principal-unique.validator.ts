import { AbstractControl, ValidationErrors } from '@angular/forms';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { IValidatorOptions } from './models/validator-options';

export interface IEmailPrincipalUniqueValidatorOptions extends IValidatorOptions {
  messagePrincipalUnique: string;
  messagePrincipalRequired: string;
}

export function emailPrincipalUniqueValidator(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IEmailPrincipalUniqueValidatorOptions,
): ValidationErrors {
  const controlValues: any[] = control.value ?? [];
  const max = options.messagePrincipalUnique;

  if (controlValues.length && controlValues.filter(c => c.principal).length === 1) {
    return;
  }

  let message: string;
  if (controlValues.some(c => c.principal)) {
    message = options.messagePrincipalUnique ? eval('`' + options.messagePrincipalUnique + '`') : `Solo puede tener un email principal`;
  } else {
    message = options.messagePrincipalRequired ? eval('`' + options.messagePrincipalRequired + '`') : `Es obligatorio que uno de los emails sea principal`;
  }

  return {
    'email-principal-unique': {
      message: message
    }
  };
}
