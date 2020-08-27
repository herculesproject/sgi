import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApartadoFormulario } from '@core/models/eti/apartado-formulario';
import { BloqueFormulario } from '@core/models/eti/bloque-formulario';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BloqueFormularioService extends SgiRestService<number, BloqueFormulario> {
  private static readonly MAPPING = '/bloqueformularios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      BloqueFormularioService.name,
      logger,
      `${environment.serviceServers.eti}${BloqueFormularioService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve los apartados de un bloque
   *
   * @param id Id del bloque
   * @param options Opciones de paginación
   */
  getApartados(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ApartadoFormulario>> {
    this.logger.debug(BloqueFormularioService.name, `getApartados(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<ApartadoFormulario, ApartadoFormulario>(`${this.endpointUrl}/${id}/apartados`, options).pipe(
      tap(() => this.logger.debug(BloqueFormularioService.name, `getApartados(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
    );
  }
}
