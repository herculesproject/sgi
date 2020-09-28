import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IApartadoFormulario } from '@core/models/eti/apartado-formulario';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApartadoFormularioService extends SgiRestService<number, IApartadoFormulario> {
  private static readonly MAPPING = '/apartadoformularios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ApartadoFormularioService.name,
      logger,
      `${environment.serviceServers.eti}${ApartadoFormularioService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve los apartados hijos de un apartado
   *
   * @param id Id del apartado
   * @param options Opciones de paginación
   */
  getHijos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IApartadoFormulario>> {
    this.logger.debug(ApartadoFormularioService.name, `getHijos(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IApartadoFormulario, IApartadoFormulario>(`${this.endpointUrl}/${id}/hijos`, options).pipe(
      tap(() => this.logger.debug(ApartadoFormularioService.name, `getHijos(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
    );
  }
}
