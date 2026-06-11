import { FormArray, FormGroup, ValidationErrors } from '@angular/forms';
import { IValidatorCompareToOptions } from './models/validator-compare-to-options';
import { IValidatorOptions } from './models/validator-options';


export function requiredChecked(formGroup: FormGroup): ValidationErrors {
  let checked = 0;
  Object.keys(formGroup.controls).forEach(key => {
    const control = formGroup.controls[key];
    if (!control.disabled && control.value === true) {
      checked++;
    }
  });
  if (checked < 1) {
    return null;
  }
  return {
    required: true,
  };
}

export function requiredRowTable(formArray: FormArray): ValidationErrors {
  return !formArray.value || formArray.length === 0 ? { oneRowRequired: true } : null;
}

export function getOptionsCompareValues(formState: any, options: IValidatorOptions & Partial<IValidatorCompareToOptions>): any {

  if (!options?.compareTo) {
    return null;
  }

  let returnValue;

  switch (options.compareTo) {
    case 'formStateProperties':
      returnValue = options.formStateProperties.map(property => getFormStateProperty(formState, property?.split('.')));
      break;
    case 'formStateProperty':
      returnValue = getFormStateProperty(formState, options.formStateProperty?.split('.'));
      break;
    case 'value':
      returnValue = options.value;
      break;
    case 'values':
      returnValue = options.values;
      break;
    default:
      console.error('Not valid options.compareTo', options.compareTo);
      returnValue = null;
      break;
  }

  return returnValue;
}

function getFormStateProperty(formState: any, propertyPath: string[]): any {
  if (!formState || !propertyPath?.length) {
    return null;
  }

  const currentPath = propertyPath.shift();

  if (propertyPath.length > 0) {
    return getFormStateProperty(formState[currentPath], propertyPath);
  }

  return formState[currentPath];
}

/**
 * Interpola en `message` los placeholders cuyo nombre esté presente en `variables`.
 *
 * Se admiten dos sintaxis: `${variable}` (forma canónica) y `{{variable}}` (aceptada por
 * compatibilidad con mensajes provenientes de integraciones externas). Solo se sustituyen nombres
 * de variable (`\w+`) del diccionario proporcionado; cualquier otro placeholder se deja intacto.
 * No se evalúan expresiones, por lo que el mensaje, aunque sea configurable externamente, no puede
 * ejecutar código.
 *
 * @param message plantilla del mensaje (puede ser null/undefined)
 * @param variables variables permitidas para la interpolación
 * @returns el mensaje interpolado, o null si no hay mensaje
 */
export function buildCustomMessage(
  message: string | null | undefined,
  variables: Record<string, unknown> = {}
): string | null {
  if (!message) {
    return null;
  }

  return message.replace(
    /\$\{(\w+)\}|\{\{(\w+)\}\}/g,
    (match, dollarName, braceName) => {
      const variableName = dollarName ?? braceName;
      return variableName in variables
        ? String(variables[variableName])
        : match;
    }
  );
}
