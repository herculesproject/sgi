import { Language } from '@core/i18n/language';
import { SgiBaseConverter } from '@sgi/framework/core';
import { I18nFieldValue } from './i18n-field';
import { I18nFieldValueResponse } from './i18n-field-response';

class I18nFieldResponseConverter
  extends SgiBaseConverter<I18nFieldValueResponse, I18nFieldValue> {
  toTarget(value: I18nFieldValueResponse): I18nFieldValue {
    if (!value) {
      return value as unknown as I18nFieldValue;
    }
    return {
      lang: Language.fromCode(value.lang),
      value: value.value
    };
  }

  fromTarget(value: I18nFieldValue): I18nFieldValueResponse {
    if (!value) {
      return value as unknown as I18nFieldValueResponse;
    }
    return {
      lang: value.lang?.code,
      value: value.value
    };
  }
}

export const I18N_FIELD_RESPONSE_CONVERTER = new I18nFieldResponseConverter();
