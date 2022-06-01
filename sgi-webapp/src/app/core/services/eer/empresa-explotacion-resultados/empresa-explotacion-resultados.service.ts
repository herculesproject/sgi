import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor,
  mixinCreate, mixinFindAll, mixinFindById,
  mixinUpdate, SgiRestBaseService, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IEmpresaExplotacionResultadosRequest } from './empresa-explotacion-resultados-request';
import { EMPRESA_EXPLOTACION_RESULTADOS_REQUEST_CONVERTER } from './empresa-explotacion-resultados-request.converter';
import { IEmpresaExplotacionResultadosResponse } from './empresa-explotacion-resultados-response';
import { EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER } from './empresa-explotacion-resultados-response.converter';


// tslint:disable-next-line: variable-name
const _EmpresaExplotacionResultadosMixinBase:
  CreateCtor<IEmpresaExplotacionResultados, IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosRequest,
    IEmpresaExplotacionResultadosResponse> &
  UpdateCtor<number, IEmpresaExplotacionResultados, IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosRequest,
    IEmpresaExplotacionResultadosResponse> &
  FindByIdCtor<number, IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosResponse> &
  FindAllCtor<IEmpresaExplotacionResultados, IEmpresaExplotacionResultadosResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          EMPRESA_EXPLOTACION_RESULTADOS_REQUEST_CONVERTER,
          EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
        ),
        EMPRESA_EXPLOTACION_RESULTADOS_REQUEST_CONVERTER,
        EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
      ),
      EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
    ),
    EMPRESA_EXPLOTACION_RESULTADOS_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class EmpresaExplotacionResultadosService extends _EmpresaExplotacionResultadosMixinBase {
  private static readonly MAPPING = '/empresas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eer}${EmpresaExplotacionResultadosService.MAPPING}`,
      http,
    );
  }

  /**
   * Comprueba si existe el grupo
   *
   * @param id Identificador del grupo
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Desactiva el grupo
   *
   * @param id Identificador del grupo
   */
  desactivar(id: number): Observable<void> {
    const url = `${this.endpointUrl}/${id}/desactivar`;
    return this.http.patch<void>(url, { id });
  }

}
