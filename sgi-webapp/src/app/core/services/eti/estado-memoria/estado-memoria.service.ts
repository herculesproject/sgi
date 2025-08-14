import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEstadoMemoria } from '@core/models/eti/estado-memoria';
import { environment } from '@env';
import { FindByIdCtor, SgiRestBaseService, mixinFindById } from '@herculesproject/framework/http';
import { IEstadoMemoriaResponse } from './estado-memoria-response';
import { ESTADO_MEMORIA_RESPONSE_CONVERTER } from './estado-memoria-response.converter';

// tslint:disable-next-line: variable-name
const _EstadoMemoriaMixinBase:
  FindByIdCtor<number, IEstadoMemoria, IEstadoMemoriaResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    ESTADO_MEMORIA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EstadoMemoriaService extends _EstadoMemoriaMixinBase {
  private static readonly MAPPING = '/estadomemorias';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EstadoMemoriaService.MAPPING}`,
      http,
    );
  }

}
