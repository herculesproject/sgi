import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { SgiRestService } from '@sgi/framework/http';
import { environment } from '@env';
import { Comite } from '@core/models/eti/comite';
import { Observable } from 'rxjs';
import { IFormulario } from '@core/models/eti/formulario';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ComiteService extends SgiRestService<number, Comite> {
  private static readonly MAPPING = '/comites';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ComiteService.name,
      logger,
      `${environment.serviceServers.eti}${ComiteService.MAPPING}`,
      http
    );
  }


  /**
   * Devuelve el formulario de tipo M10, M20 o M30 del comité asociado al id .
   *
   * @param id Id del comité
   */
  findComiteFormularioTipoM(id: number): Observable<IFormulario> {
    this.logger.debug(ComiteService.name, `findComiteFormulario(${id})`, '-', 'start');
    return this.http.get<IFormulario>(`${this.endpointUrl}/${id}/comite-formulario`).pipe(
      tap(() => this.logger.debug(ComiteService.name, `findComiteFormulario(${id})`, '-', 'end'))
    );
  }
}
