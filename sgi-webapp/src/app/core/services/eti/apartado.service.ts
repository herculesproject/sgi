import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IApartado } from '@core/models/eti/apartado';
import { environment } from '@env';
import { SgiReadOnlyRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApartadoService extends SgiReadOnlyRestService<number, IApartado> {
  private static readonly MAPPING = '/apartados';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ApartadoService.name,
      logger,
      `${environment.serviceServers.eti}${ApartadoService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve los apartados hijos de un apartado
   *
   * @param id Id del apartado
   * @param options Opciones de paginación
   */
  getHijos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IApartado>> {
    return this.find<IApartado, IApartado>(`${this.endpointUrl}/${id}/hijos`, options);
  }
}
