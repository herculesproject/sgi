import { Injectable } from '@angular/core';
import { SgiRestService } from '@sgi/framework/http';
import { Evaluacion } from '@core/models/eti/evaluacion';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EvaluacionService extends SgiRestService<number, Evaluacion>{
  private static readonly MAPPING = '/evaluaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      EvaluacionService.name,
      logger,
      `${environment.serviceServers.eti}${EvaluacionService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve todos las evaluaciones por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findAllByConvocatoriaReunionId(idConvocatoria: number) {
    this.logger.debug(EvaluacionService.name, `findAllByConvocatoriaReunionId(${idConvocatoria})`, '-', 'START');
    return this.find<Evaluacion, Evaluacion>(`${this.endpointUrl}/convocatoriareunion/${idConvocatoria}`, null).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `findAllByConvocatoriaReunionId(${idConvocatoria})`, '-', 'END'))
    );
  }
}
