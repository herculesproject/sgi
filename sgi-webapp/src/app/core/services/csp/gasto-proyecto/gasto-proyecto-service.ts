import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of } from 'rxjs';
import { IGastoProyectoResponse } from './gasto-proyecto-response';
import { GASTO_PROYECTO_RESPONSE_CONVERTER } from './gasto-proyecto-response.converter';

// tslint:disable-next-line: variable-name
const _GastoProyectoServiceMixinBase:
  FindAllCtor<IGastoProyecto, IGastoProyectoResponse> &
  typeof SgiRestBaseService = mixinFindAll(SgiRestBaseService, GASTO_PROYECTO_RESPONSE_CONVERTER);

@Injectable({
  providedIn: 'root'
})
export class GastoProyectoService extends _GastoProyectoServiceMixinBase {
  private static readonly MAPPING = '/gastos-proyectos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GastoProyectoService.MAPPING}`,
      http,
    );
  }

  // TODO: Remove when implemented
  findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<IGastoProyecto>> {
    return of({ items: [] } as SgiRestListResult<IGastoProyecto>);
  }

}
