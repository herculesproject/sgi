import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoFinanciacion } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TipoFinanciacionService extends SgiRestService<number, ITipoFinanciacion> {
  private static readonly MAPPING = '/tipofinanciaciones';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoFinanciacionService.name,
      logger,
      `${environment.serviceServers.csp}${TipoFinanciacionService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoFinanciacion>> {
    return this.find<ITipoFinanciacion, ITipoFinanciacion>(`${this.endpointUrl}/todos`, options);
  }


  /**
   * Desactiva un tipo de financiacion
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

  /**
   * Reactivar fuentes de financiación
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, { id });
  }


}

