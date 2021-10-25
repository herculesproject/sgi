import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IReparto } from '@core/models/pii/reparto';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { IRepartoResponse } from './reparto-response';
import { REPARTO_RESPONSE_CONVERTER } from './reparto-response.converter';

// tslint:disable-next-line: variable-name
const _RepartoServiceMixinBase:
  FindAllCtor<IReparto, IRepartoResponse> &
  FindByIdCtor<number, IReparto, IRepartoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      REPARTO_RESPONSE_CONVERTER
    ),
    REPARTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class RepartoService extends _RepartoServiceMixinBase {

  private static readonly MAPPING = '/repartos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${RepartoService.MAPPING}`,
      http,
    );
  }
}
