import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { SgiRestService, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { IDictamen } from '@core/models/eti/dictamen';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class TipoEvaluacionService extends SgiRestService<number, TipoEvaluacion> {
  private static readonly MAPPING = '/tipoevaluaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoEvaluacionService.name,
      logger,
      `${environment.serviceServers.eti}${TipoEvaluacionService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve el listado de dictamenes dependiendo
   * del tipo de Evaluación y si la Evaluación es de revisión mínima
   */

  findAllDictamenByTipoEvaluacionAndRevisionMinima(
    idTipoEvaluacion: number, esRevisionMinima: boolean): Observable<SgiRestListResult<IDictamen>> {
    this.logger.debug(TipoEvaluacionService.name, `findAllDictamenByTipoEvaluacionAndRevisionMinima()`, '-', 'start');
    return this.find<IDictamen, IDictamen>(`${this.endpointUrl}/${idTipoEvaluacion}/dictamenes-revision-minima/${esRevisionMinima}`, null)
      .pipe(
        tap(() => this.logger.debug(TipoEvaluacionService.name, `findAllDictamenByTipoEvaluacionAndRevisionMinima()`, '-', 'end'))
      );
  }
}
