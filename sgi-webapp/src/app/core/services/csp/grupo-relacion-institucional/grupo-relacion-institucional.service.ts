import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@herculesproject/framework/http';
import { Observable } from 'rxjs';
import { IGrupoRelacionInstitucionalRequest } from './grupo-relacion-institucional-request';
import { GRUPO_RELACION_INSTITUCIONAL_REQUEST_CONVERTER } from './grupo-relacion-institucional-request.converter';
import { IGrupoRelacionInstitucionalResponse } from './grupo-relacion-institucional-response';
import { GRUPO_RELACION_INSTITUCIONAL_RESPONSE_CONVERTER } from './grupo-relacion-institucional-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoRelacionInstitucionalMixinBase:
  CreateCtor<
    IGrupoRelacionInstitucional,
    IGrupoRelacionInstitucional,
    IGrupoRelacionInstitucionalRequest,
    IGrupoRelacionInstitucionalResponse
  > &
  UpdateCtor<
    number,
    IGrupoRelacionInstitucional,
    IGrupoRelacionInstitucional,
    IGrupoRelacionInstitucionalRequest,
    IGrupoRelacionInstitucionalResponse
  > &
  FindByIdCtor<number, IGrupoRelacionInstitucional, IGrupoRelacionInstitucionalResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        GRUPO_RELACION_INSTITUCIONAL_REQUEST_CONVERTER,
        GRUPO_RELACION_INSTITUCIONAL_RESPONSE_CONVERTER
      ),
      GRUPO_RELACION_INSTITUCIONAL_REQUEST_CONVERTER,
      GRUPO_RELACION_INSTITUCIONAL_RESPONSE_CONVERTER
    ),
    GRUPO_RELACION_INSTITUCIONAL_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoRelacionInstitucionalService extends _GrupoRelacionInstitucionalMixinBase {
  private static readonly MAPPING = '/gruporelacionesinstitucionales';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoRelacionInstitucionalService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
