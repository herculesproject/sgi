import { AbstractControl, ValidationErrors } from '@angular/forms';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { IValidatorCompareToOptions } from './models/validator-compare-to-options';
import { IValidatorOptions } from './models/validator-options';
import { getOptionsCompareValues as getCompareValue } from './utils.validator';

export interface IDateValidatorOptions extends IValidatorOptions, Partial<Pick<IValidatorCompareToOptions, 'formStateProperty' | 'value'>> {
}

export function dateIsAfter(
  control: AbstractControl,
  field: SgiFormlyFieldConfig,
  options: IDateValidatorOptions,
): ValidationErrors {
  const valueToCompare = getCompareValue(field.options.formState, options);
  if (!valueToCompare || control.value > valueToCompare) {
    return null;
  }

  return {
    'date-is-after-validator': {
      message: options.message ? eval('`' + options.message + '`') : `La fecha debe de ser posterior al ${valueToCompare}`
    }
  };
}