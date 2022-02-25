import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICampoProduccionCientifica } from '@core/models/prc/campo-produccion-cientifica';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { environment } from '@env';
import {
  FindAllCtor, mixinFindAll,
  RSQLSgiRestFilter, RSQLSgiRestSort,
  SgiRestBaseService, SgiRestFilterOperator,
  SgiRestFindOptions, SgiRestSortDirection
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IValorCampoResponse } from './valor-campo-response';
import { VALOR_CAMPO_RESPONSE_CONVERTER } from './valor-campo-response.converter';

// tslint:disable-next-line: variable-name
const _ValorCampoServiceMixinBase:
  FindAllCtor<IValorCampo, IValorCampoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    VALOR_CAMPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ValorCampoService extends _ValorCampoServiceMixinBase {

  private static readonly MAPPING = '/valores-campos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ValorCampoService.MAPPING}`,
      http,
    );
  }

  findAllValorCampo = (campoProduccionCientifica: ICampoProduccionCientifica): Observable<IValorCampo[]> => {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter(
        'campoProduccionCientificaId', SgiRestFilterOperator.EQUALS, campoProduccionCientifica?.id?.toString()
      ),
      sort: new RSQLSgiRestSort('orden', SgiRestSortDirection.ASC)
    };
    return this.findAll(options).pipe(
      map(response => response.items),
      map(items => items.map(item => {
        item.campoProduccionCientifica = campoProduccionCientifica;
        return item;
      }))
    );
  }
}
