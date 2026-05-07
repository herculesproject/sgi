import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoGrupo } from '@core/models/csp/tipo-grupo';
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
import { ITipoGrupoRequest } from './tipo-grupo-request';
import { TIPO_GRUPO_REQUEST_CONVERTER } from './tipo-grupo-request.converter';
import { ITipoGrupoResponse } from './tipo-grupo-response';
import { TIPO_GRUPO_RESPONSE_CONVERTER } from './tipo-grupo-response.converter';

// tslint:disable-next-line: variable-name
const _TipoGrupoServiceMixinBase:
  CreateCtor<ITipoGrupo, ITipoGrupo, ITipoGrupoRequest, ITipoGrupoResponse> &
  UpdateCtor<number, ITipoGrupo, ITipoGrupo, ITipoGrupoRequest, ITipoGrupoResponse> &
  FindByIdCtor<number, ITipoGrupo, ITipoGrupoResponse> &
  FindAllCtor<ITipoGrupo, ITipoGrupoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_GRUPO_REQUEST_CONVERTER,
          TIPO_GRUPO_RESPONSE_CONVERTER
        ),
        TIPO_GRUPO_REQUEST_CONVERTER,
        TIPO_GRUPO_RESPONSE_CONVERTER
      ),
      TIPO_GRUPO_RESPONSE_CONVERTER
    ),
    TIPO_GRUPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoGrupoService extends _TipoGrupoServiceMixinBase {
  private static readonly MAPPING = '/tiposgrupo';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${TipoGrupoService.MAPPING}`,
      http,
    );
  }

  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoGrupo>> {
    return this.find<ITipoGrupoResponse, ITipoGrupo>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_GRUPO_RESPONSE_CONVERTER
    );
  }

  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
