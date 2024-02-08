import { IFormly } from '@core/models/eti/formly';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFormlyResponse } from './formly-response';

class FormlyResponseConverter extends SgiBaseConverter<IFormlyResponse, IFormly>{
  toTarget(value: IFormlyResponse): IFormly {
    if (!value) {
      return value as unknown as IFormly;
    }
    return {
      id: value.id,
      version: value.version,
      formlyNombres: value.formlyNombres
    };
  }
  fromTarget(value: IFormly): IFormlyResponse {
    if (!value) {
      return value as unknown as IFormlyResponse;
    }
    return {
      id: value.id,
      version: value.version,
      formlyNombres: value.formlyNombres
    };
  }
}

export const FORMLY_RESPONSE_CONVERTER = new FormlyResponseConverter();
