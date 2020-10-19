import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestListResult, SgiRestFindOptions } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { IComiteFormulario } from '@core/models/eti/comite-formulario';
import { IBloqueFormulario } from '@core/models/eti/bloque-formulario';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ComiteFormularioService extends SgiRestService<number, IComiteFormulario> {

  private static readonly MAPPING = '/comiteformularios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ComiteFormularioService.name,
      logger,
      `${environment.serviceServers.eti}${ComiteFormularioService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera los bloques de formulario de un comité dependiendo del tipo de evaluación.
   * @param idComite Identificador de comité.
   * @param idTipoEvaluacion  Identificador del tipo de evaluación.
   * @param options opciones de búsqueda
   * @return Listado paginado de bloques formularios.
   */
  findBloqueFormulario(
    idComite: number,
    idTipoEvaluacion: number,
    options?: SgiRestFindOptions): Observable<SgiRestListResult<IBloqueFormulario>> {
    this.logger.debug(ComiteFormularioService.name, `findBloqueFormulario(${idComite}, ${idTipoEvaluacion}, ${options ? JSON.stringify(options) : ''})`, '-', 'START');
    return this.find<IBloqueFormulario, IBloqueFormulario>
      (`${this.endpointUrl}/${idComite}/bloque-formularios/${idTipoEvaluacion}`, options).pipe(
        tap(() => this.logger.debug(ComiteFormularioService.name, `findBloqueFormulario(${idComite}, ${idTipoEvaluacion}, ${options ? JSON.stringify(options) : ''})`, '-', 'END'))
      );
  }
}
