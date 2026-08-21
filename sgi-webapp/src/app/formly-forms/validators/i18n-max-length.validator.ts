import { AbstractControl } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nValidators } from '@core/validators/i18n-validator';
import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { TranslateService } from '@ngx-translate/core';

const MSG_FORMLY_VALIDATIONS_I18N_MAX_LENGTH = marker('msg.formly.validations.i18n-max-length');

/**
 * Validador de longitud máxima para los tipos formly cuyo valor es un array de traducciones
 * (`i18n-input`, `i18n-textarea`, `i18n-ckeditor`).
 *
 * El límite se declara en `templateOptions.maxLength` y se aplica a cada idioma de forma
 * independiente, contando la longitud del valor almacenado (en `i18n-ckeditor` incluye el
 * HTML generado por el editor).
 */
export function i18nMaxLength(translate: TranslateService) {
  return {
    expression: (control: AbstractControl, field: SgiFormlyFieldConfig): boolean =>
      !field.templateOptions?.maxLength || !I18nValidators.maxLength(field.templateOptions.maxLength)(control),
    message: (error: any, field: SgiFormlyFieldConfig): string => translate.instant(
      MSG_FORMLY_VALIDATIONS_I18N_MAX_LENGTH,
      { max: field.templateOptions.maxLength }
    )
  };
}
