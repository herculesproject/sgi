import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IComentario } from '@core/models/eti/comentario';
import { IEvaluacionSolicitante } from '@core/models/eti/evaluacion-solicitante';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { environment } from '@env';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EvaluacionService extends SgiRestService<number, IEvaluacion>{
  private static readonly MAPPING = '/evaluaciones';
  private static readonly CONVERTER_EVALUACIONES_SOLICITANTES = new class extends SgiBaseConverter<IEvaluacion, IEvaluacionSolicitante> {
    toTarget(value: IEvaluacion): IEvaluacionSolicitante {
      return {
        id: value.id,
        memoria: value.memoria,
        comite: value.comite,
        convocatoriaReunion: value.convocatoriaReunion,
        tipoEvaluacion: value.tipoEvaluacion,
        version: value.version,
        dictamen: value.dictamen,
        fechaDictamen: value.fechaDictamen,
        esRevMinima: value.esRevMinima,
        activo: value.activo,
        persona: null,
        evaluador1: value.evaluador1,
        evaluador2: value.evaluador2
      };
    }
    fromTarget(value: IEvaluacionSolicitante): IEvaluacion {
      return {
        id: value.id,
        memoria: value.memoria,
        comite: value.comite,
        convocatoriaReunion: value.convocatoriaReunion,
        tipoEvaluacion: value.tipoEvaluacion,
        version: value.version,
        dictamen: value.dictamen,
        fechaDictamen: value.fechaDictamen,
        esRevMinima: value.esRevMinima,
        activo: value.activo,
        evaluador1: value.evaluador1,
        evaluador2: value.evaluador2
      };
    }
  }();

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(EvaluacionService.name, logger, `${environment.serviceServers.eti}${EvaluacionService.MAPPING}`, http);
  }

  /**
   * Devuelve todos las evaluaciones por convocatoria id.
   *
   * @param convocatoriaId id convocatoria.
   */
  findAllByConvocatoriaReunionId(convocatoriaId: number): Observable<SgiRestListResult<IEvaluacion>> {
    this.logger.debug(EvaluacionService.name, `findAllByConvocatoriaReunionId(${convocatoriaId})`, '-', 'start');
    return this.find<IEvaluacion, IEvaluacion>(`${this.endpointUrl}/convocatoriareuniones/${convocatoriaId}`, null).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `findAllByConvocatoriaReunionId(${convocatoriaId})`, '-', 'end'))
    );
  }

  /**
   * Devuelve los comentarios de una evalución
   *
   * @param id Id de la evaluación
   * @param options Opciones de paginación
   */
  getComentarios(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IComentario>> {
    this.logger.debug(EvaluacionService.name, `findByEvaluacionId(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IComentario, IComentario>(`${this.endpointUrl}/${id}/comentarios`, options).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `findByEvaluacionId(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
    );
  }

  /**
   * Obtiene el número total de comentarios que tiene una evaluación
   *
   * @param id Id de la evaluación
   */
  countComentarios(id: number): Observable<number> {
    this.logger.debug(EvaluacionService.name, `countByEvaluacionId(${id})`, '-', 'start');
    return this.http.get<number>(`${this.endpointUrl}/${id}/comentarios/count`).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `countByEvaluacionId(${id})`, '-', 'end'))
    );
  }

  /**
   * Añade un listado de comentarios a una evaluación
   *
   * @param id Id de la evaluación
   * @param comentarios Comentarios a crear
   */
  createComentarios(id: number, comentarios: IComentario[]): Observable<IComentario[]> {
    this.logger.debug(EvaluacionService.name, `createComentarios(${id}, ${comentarios})`, '-', 'start');
    return this.http.post<IComentario[]>(`${this.endpointUrl}/${id}/comentarios`, comentarios).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `createComentarios(${id}, ${comentarios})`, '-', 'end'))
    );
  }

  /**
   * Actualiza un listado de comentarios de una evaluación
   *
   * @param id Id de la evaluación
   * @param comentarios Comentarios a actualizar
   */
  updateComentarios(id: number, comentarios: IComentario[]): Observable<IComentario[]> {
    this.logger.debug(EvaluacionService.name, `updateComentarios(${id}, ${comentarios})`, '-', 'start');
    return this.http.put<IComentario[]>(`${this.endpointUrl}/${id}/comentarios`, comentarios).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `updateComentarios(${id}, ${comentarios})`, '-', 'end'))
    );
  }

  /**
   * Elimina un listado de comentarios de una evaluación
   *
   * @param id Id de la evaluación
   * @param ids Listado de ids de los comentarios
   */
  deleteComentarios(id: number, ids: number[]): Observable<void> {
    this.logger.debug(EvaluacionService.name, `deleteComentarios(${id}, ${ids})`, '-', 'start');
    const params = new HttpParams().set('ids', ids.toString());
    return this.http.delete<void>(`${this.endpointUrl}/${id}/comentarios`, { params }).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `deleteComentarios(${id}, ${ids})`, '-', 'end'))
    );
  }

  /**
   * Por un lado devuelve las evaluaciones de tipo Memoria, con las memorias en estado "En Evaluacion" o "En secretaría revisión mínima",
   * y por otro, las evaluaciones de tipo Retrospectiva cuya memoria tenga en estado de la retrospectiva "En Evaluacion",
   * ambas en su última versión (que serán las que no estén evaluadas).
   * @param options SgiRestFindOptions.
   */
  findAllByMemoriaAndRetrospectivaEnEvaluacion(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacionSolicitante>> {
    this.logger.debug(EvaluacionService.name, `findAllByMemoriaAndRetrospectivaEnEvaluacion
    (${options ? JSON.stringify(options) : ''})`, '-', 'START');
    return this.find<IEvaluacion, IEvaluacionSolicitante>(`${environment.serviceServers.eti}${EvaluacionService.MAPPING}/evaluables`,
      options, EvaluacionService.CONVERTER_EVALUACIONES_SOLICITANTES).pipe(
        tap(() => {
          this.logger.debug(EvaluacionService.name, `findAllByMemoriaAndRetrospectivaEnEvaluacion(${options ? JSON.stringify(options) : ''})`, '-', 'END');
        }));
  }

  /**
   * Devuelve todos las evaluaciones de la convocatoria que no son revisión mínima.
   *
   * @param idConvocatoria id convocatoria.
   * @param options opcione de busqueda.
   * @return las evaluaciones de la convocatoria.
   */
  findAllByConvocatoriaReunionIdAndNoEsRevMinima(idConvocatoria: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IEvaluacion>> {
    this.logger.debug(EvaluacionService.name, `findAllByConvocatoriaReunionIdAndNoEsRevMinima(${idConvocatoria})`, '-', 'START');
    return this.find<IEvaluacion, IEvaluacion>(`${this.endpointUrl}/convocatoriareunionnorevminima/${idConvocatoria}`, options).pipe(
      tap(() => this.logger.debug(EvaluacionService.name, `findAllByConvocatoriaReunionIdAndNoEsRevMinima(${idConvocatoria})`, '-', 'END'))
    );
  }

  /**
   * Devuelve todos las memorias de evaluación que tengan determinados estados.
   *
   * @param options opciones de búsqueda.
   * @return las evaluaciones
   */

  findSeguimientoMemoria(options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacionSolicitante>> {
    this.logger.debug(EvaluacionService.name, `findSeguimientoMemoria
    (${options ? JSON.stringify(options) : ''})`, '-', 'START');
    return this.find<IEvaluacion, IEvaluacionSolicitante>(`${environment.serviceServers.eti}${EvaluacionService.MAPPING}/memorias-seguimiento-final`,
      options, EvaluacionService.CONVERTER_EVALUACIONES_SOLICITANTES).pipe(
        tap(() => {
          this.logger.debug(EvaluacionService.name, `findSeguimientoMemoria(${options ? JSON.stringify(options) : ''})`, '-', 'END');
        }));
  }

}
