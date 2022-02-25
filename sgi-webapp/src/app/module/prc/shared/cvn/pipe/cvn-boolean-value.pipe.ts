import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Pipe, PipeTransform } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IValorCampo } from '@core/models/prc/valor-campo';

const MSG_TRUE = marker('label.si');
const MSG_FALSE = marker('label.no');

@Pipe({
  name: 'cvnBooleanValue'
})
export class CvnBooleanValuePipe implements PipeTransform {

  transform([firstElement]: IValorCampo[]): string {
    if (!firstElement) {
      return '';
    }
    return coerceBooleanProperty(firstElement.valor) ? MSG_TRUE : MSG_FALSE;
  }

}
