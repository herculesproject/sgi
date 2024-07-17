import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IBloque } from '@core/models/eti/bloque';
import { IFormulario } from '@core/models/eti/formulario';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IBloqueResponse } from './bloque/bloque-response';
import { BLOQUE_RESPONSE_CONVERTER } from './bloque/bloque-response.converter';
import { IFormularioResponse } from './formulario/formulario-response';
import { FORMULARIO_RESPONSE_CONVERTER } from './formulario/formulario-response.converter';

// tslint:disable-next-line: variable-name
const _FormularioServiceMixinBase:
  FindByIdCtor<number, IFormulario, IFormularioResponse> &
  FindAllCtor<IFormulario, IFormularioResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      FORMULARIO_RESPONSE_CONVERTER
    ),
    FORMULARIO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class FormularioService extends _FormularioServiceMixinBase {

  private static readonly MAPPING = '/formularios';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${FormularioService.MAPPING}`,
      http
    );
  }

  getBloques(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IBloque>> {
    return this.find<IBloqueResponse, IBloque>(`${this.endpointUrl}/${id}/bloques`, options, BLOQUE_RESPONSE_CONVERTER);
  }

  /**
   * Actualiza el estado de la memoria o de la retrospectiva al estado final
   * correspondiente al tipo de formulario completado.
   *
   * @param memoriaId identificador de la memoria
   * @param tipoFormulario tipo de formulario
   */
  completado(memoriaId: number, tipoFormulario: number): Observable<void> {
    const url = `${this.endpointUrl}/completado`;
    let params = new HttpParams();
    params = params.append('memoriaId', memoriaId?.toString());
    params = params.append('tipoFormulario', tipoFormulario?.toString());

    return this.http.head<void>(url, { params });
  }

}
