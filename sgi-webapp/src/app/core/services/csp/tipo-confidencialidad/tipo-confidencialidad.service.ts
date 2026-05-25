import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoConfidencialidad } from '@core/models/csp/tipo-confidencialidad';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate,
  SgiRestBaseService,
  SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { ITipoConfidencialidadRequest } from './tipo-confidencialidad-request';
import { TIPO_CONFIDENCIALIDAD_REQUEST_CONVERTER } from './tipo-confidencialidad-request.converter';
import { ITipoConfidencialidadResponse } from './tipo-confidencialidad-response';
import { TIPO_CONFIDENCIALIDAD_RESPONSE_CONVERTER } from './tipo-confidencialidad-response.converter';

// tslint:disable-next-line: variable-name
const _TipoConfidencialidadServiceMixinBase:
  CreateCtor<ITipoConfidencialidad, ITipoConfidencialidad, ITipoConfidencialidadRequest, ITipoConfidencialidadResponse> &
  UpdateCtor<number, ITipoConfidencialidad, ITipoConfidencialidad, ITipoConfidencialidadRequest, ITipoConfidencialidadResponse> &
  FindByIdCtor<number, ITipoConfidencialidad, ITipoConfidencialidadResponse> &
  FindAllCtor<ITipoConfidencialidad, ITipoConfidencialidadResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_CONFIDENCIALIDAD_REQUEST_CONVERTER,
          TIPO_CONFIDENCIALIDAD_RESPONSE_CONVERTER
        ),
        TIPO_CONFIDENCIALIDAD_REQUEST_CONVERTER,
        TIPO_CONFIDENCIALIDAD_RESPONSE_CONVERTER
      ),
      TIPO_CONFIDENCIALIDAD_RESPONSE_CONVERTER
    ),
    TIPO_CONFIDENCIALIDAD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoConfidencialidadService extends _TipoConfidencialidadServiceMixinBase {
  private static readonly MAPPING = '/tiposconfidencialidad';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoConfidencialidadService.MAPPING}`,
      http,
    );
  }

  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoConfidencialidad>> {
    return this.find<ITipoConfidencialidadResponse, ITipoConfidencialidad>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_CONFIDENCIALIDAD_RESPONSE_CONVERTER
    );
  }

  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
