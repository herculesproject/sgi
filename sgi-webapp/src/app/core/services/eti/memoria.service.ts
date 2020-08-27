import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IEvaluacionWithNumComentario } from '@core/models/eti/dto/evaluacion-with-num-comentarios';
import { Memoria } from '@core/models/eti/memoria';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MemoriaService extends SgiRestService<number, Memoria>{
  private static readonly MAPPING = '/memorias';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(MemoriaService.name, logger, `${environment.serviceServers.eti}${MemoriaService.MAPPING}`, http);
  }

  /**
   * Devuelve toda la documentación asociada a una memoria.
   *
   * @param id id de la memoria.
   */
  getDocumentaciones(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<DocumentacionMemoria>> {
    this.logger.debug(MemoriaService.name, `getDocumentaciones(${id}, ${options ? JSON.stringify(options) : options})`, 'start');
    return this.find<DocumentacionMemoria, DocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentaciones`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `getDocumentaciones(${id}, ${options ? JSON.stringify(options) : options})`, 'end'))
      );
  }

  /**
   * Devuelve todas las evaluaciones de una memoria.
   *
   * @param memoriaId id de la memoria.
   * @param evaluacionId id de la evaluacion.
   */
  getEvaluaciones(memoriaId: number, evaluacionId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IEvaluacionWithNumComentario>> {
    this.logger.debug(MemoriaService.name,
      `getEvaluaciones(${memoriaId}, ${evaluacionId}, ${options ? JSON.stringify(options) : options})`, '-', 'start');
    return this.find<IEvaluacionWithNumComentario, IEvaluacionWithNumComentario>
      (`${this.endpointUrl}/${memoriaId}/evaluaciones-anteriores/${evaluacionId}`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `getEvaluaciones(${memoriaId}, ${evaluacionId}, ${options ? JSON.stringify(options) : options})`, '-', 'end'))
      );
  }
}
