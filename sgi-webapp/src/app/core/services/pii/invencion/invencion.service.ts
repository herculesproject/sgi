import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInvencion } from '@core/models/pii/invencion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IInvencionRequest } from './invencion-request';
import { INVENCION_REQUEST_CONVERTER } from './invencion-request.converter';
import { IInvencionResponse } from './invencion-response';
import { INVENCION_RESPONSE_CONVERTER } from './invencion-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionServiceMixinBase:
  FindAllCtor<IInvencion, IInvencionResponse> &
  FindByIdCtor<number, IInvencion, IInvencionResponse> &
  CreateCtor<IInvencion, IInvencion, IInvencionRequest, IInvencionResponse> &
  UpdateCtor<number, IInvencion, IInvencion, IInvencionRequest, IInvencionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          INVENCION_REQUEST_CONVERTER,
          INVENCION_RESPONSE_CONVERTER
        ),
        INVENCION_REQUEST_CONVERTER,
        INVENCION_RESPONSE_CONVERTER
      ),
      INVENCION_RESPONSE_CONVERTER
    ),
    INVENCION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionService extends _InvencionServiceMixinBase {
  private static readonly MAPPING = '/invenciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InvencionService.MAPPING}`,
      http,
    );
  }

  /**
 * Muestra activos y no activos
 *
 * @param options opciones de búsqueda.
 */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IInvencion>> {
    return this.find<IInvencionResponse, IInvencion>(
      `${this.endpointUrl}/todos`,
      options,
      INVENCION_RESPONSE_CONVERTER
    );
  }

  /**
 * Comprueba si existe una invencion
 *
 * @param id Id de la invencion
 */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }
}
