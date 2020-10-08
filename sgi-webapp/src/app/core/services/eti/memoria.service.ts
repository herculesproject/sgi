import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IEvaluacionWithNumComentario } from '@core/models/eti/evaluacion-with-num-comentarios';
import { IMemoria } from '@core/models/eti/memoria';
import { environment } from '@env';
import { SgiRestFindOptions, SgiRestListResult, SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoriaPeticionEvaluacion';
import { IEvaluacion } from '@core/models/eti/evaluacion';

@Injectable({
  providedIn: 'root'
})
export class MemoriaService extends SgiRestService<number, IMemoria>{
  private static readonly MAPPING = '/memorias';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(MemoriaService.name, logger, `${environment.serviceServers.eti}${MemoriaService.MAPPING}`, http);
  }

  /**
   * Devuelve toda la documentación asociada a una memoria.
   *
   * @param id id de la memoria.
   */
  getDocumentaciones(
    idMemoria: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    this.logger.debug(MemoriaService.name, `getDocumentaciones(${idMemoria}, ${options ? JSON.stringify(options) : options})`, 'start');
    return this.find<IDocumentacionMemoria, IDocumentacionMemoria>(
      `${this.endpointUrl}/${idMemoria}/documentaciones`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `getDocumentaciones(${idMemoria},  ${options ? JSON.stringify(options) : options})`, 'end'))
      );
  }

  /**
   * Devuelve toda la documentación asociada a una memoria según el tipo de la Evaluación
   *
   * @param id id de la memoria.
   * @param idTipoEvaluacion id del tipo de la evaluación.
   */
  getDocumentacionesTipoEvaluacion(
    idMemoria: number, idTipoEvaluacion: number,
    options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    this.logger.debug(MemoriaService.name, `getDocumentacionesTipoEvaluacion(${idMemoria},${idTipoEvaluacion}, ${options ? JSON.stringify(options) : options})`, 'start');
    return this.find<IDocumentacionMemoria, IDocumentacionMemoria>(
      `${this.endpointUrl}/${idMemoria}/documentaciones/${idTipoEvaluacion}`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `getDocumentacionesTipoEvaluacion(${idMemoria}, ${idTipoEvaluacion}, ${options ? JSON.stringify(options) : options})`, 'end'))
      );
  }

  /**
   * Devuelve todas las evaluaciones de una memoria.
   *
   * @param memoriaId id de la memoria.
   * @param evaluacionId id de la evaluacion.
   */
  getEvaluacionesAnteriores(memoriaId: number, evaluacionId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IEvaluacionWithNumComentario>> {
    this.logger.debug(MemoriaService.name,
      `getEvaluacionesAnteriores(${memoriaId}, ${evaluacionId}, ${options ? JSON.stringify(options) : options})`, '-', 'start');
    return this.find<IEvaluacionWithNumComentario, IEvaluacionWithNumComentario>
      (`${this.endpointUrl}/${memoriaId}/evaluaciones-anteriores/${evaluacionId}`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `getEvaluacionesAnteriores(${memoriaId}, ${evaluacionId}, ${options ? JSON.stringify(options) : options})`, '-', 'end'))
      );
  }

  /**
   * Devuelve todos las memorias asignables a la convocatoria
   *
   * @param idConvocatoria id convocatoria.
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria.
   */
  findAllMemoriasAsignablesConvocatoria(idConvocatoria: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IMemoria>> {
    this.logger.debug(MemoriaService.name, `findAllMemoriasAsignablesConvocatoria(${idConvocatoria})`, '-', 'START');
    return this.find<IMemoria, IMemoria>(`${this.endpointUrl}/asignables/${idConvocatoria}`, options).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `findAllMemoriasAsignablesConvocatoria(${idConvocatoria})`, '-', 'END'))
    );
  }

  /**
   * Devuelve todos las memorias asignables para una convocatoria de tipo seguimiento
   *
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria de ese tipo seguimiento.
   */
  findAllAsignablesTipoConvocatoriaSeguimiento(options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IMemoria>> {
    this.logger.debug(MemoriaService.name, `findAllAsignablesTipoConvocatoriaSeguimiento()`, '-', 'START');
    return this.find<IMemoria, IMemoria>(`${this.endpointUrl}/tipo-convocatoria-seg`, options).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `findAllAsignablesTipoConvocatoriaSeguimiento()`, '-', 'END'))
    );
  }


  /**
   * Devuelve todos las memorias asignables para una convocatoria de tipo ordinaria / extraordinaria
   *
   * @param options opciones de busqueda.
   * @return las memorias asignables a la convocatoria de tipo ordinaria / extraordinaria.
   *
   */
  findAllAsignablesTipoConvocatoriaOrdExt(options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IMemoria>> {
    this.logger.debug(MemoriaService.name, `findAllAsignablesTipoConvocatoriaOrdExt()`, '-', 'START');
    return this.find<IMemoria, IMemoria>(`${this.endpointUrl}/tipo-convocatoria-ord-ext`, options).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `findAllAsignablesTipoConvocatoriaOrdExt()`, '-', 'END'))
    );
  }

  findAllByPersonaRef(options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IMemoria>> {
    this.logger.debug(MemoriaService.name, `findAllByPersonaRef()`, '-', 'START');
    return this.find<IMemoria, IMemoria>(`${this.endpointUrl}/persona`, options).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `findAllByPersonaRef()`, '-', 'END'))
    );
  }

  findAll(options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    this.logger.debug(MemoriaService.name, `findAll()`, '-', 'START');
    return this.find<IMemoriaPeticionEvaluacion, IMemoriaPeticionEvaluacion>(`${this.endpointUrl}`, options).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `findAll()`, '-', 'END'))
    );
  }
  /**
   * Devuelve toda la documentación por tipo de formulario de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionFormulario(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    this.logger.debug(MemoriaService.name, `findDocumentacionFormulario(${id}, ${options ? JSON.stringify(options) : options})`, 'start');
    return this.find<IDocumentacionMemoria, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-formulario`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `findDocumentacionFormulario(${id}, ${options ? JSON.stringify(options) : options})`, 'end'))
      );
  }


  /**
   * Devuelve toda la documentación por tipo de seguimiento anual de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionSeguimientoAnual(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    this.logger.debug(MemoriaService.name, `findDocumentacionSeguimientoAnual(${id}, ${options ? JSON.stringify(options) : options})`, 'start');
    return this.find<IDocumentacionMemoria, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-anual`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `findDocumentacionSeguimientoAnual(${id}, ${options ? JSON.stringify(options) : options})`, 'end'))
      );
  }


  /**
   * Devuelve toda la documentación por tipo de seguimiento final de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionSeguimientoFinal(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    this.logger.debug(MemoriaService.name, `findDocumentacionSeguimientoFinal(${id}, ${options ? JSON.stringify(options) : options})`, 'start');
    return this.find<IDocumentacionMemoria, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-seguimiento-final`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `findDocumentacionSeguimientoFinal(${id}, ${options ? JSON.stringify(options) : options})`, 'end'))
      );
  }


  /**
   * Devuelve toda la documentación por tipo de retrospectiva de una memoria.
   *
   * @param id id de la memoria.
   */
  findDocumentacionRetrospectiva(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IDocumentacionMemoria>> {
    this.logger.debug(MemoriaService.name,
      `findDocumentacionRetrospectiva(${id}, ${options ? JSON.stringify(options) : options})`, 'start');
    return this.find<IDocumentacionMemoria, IDocumentacionMemoria>(
      `${this.endpointUrl}/${id}/documentacion-retrospectiva`, options).pipe(
        tap(() => this.logger.debug(MemoriaService.name,
          `findDocumentacionRetrospectiva(${id}, ${options ? JSON.stringify(options) : options})`, 'end'))
      );
  }


  createDocumentacionInicial(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    this.logger.debug(MemoriaService.name, `createDocumentacionInicial(${id}, ${documentacionMemoria})`, '-', 'start');
    return this.http.post<IDocumentacionMemoria>(`${this.endpointUrl}/${id}/documentacion-inicial`, documentacionMemoria).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `createDocumentacionInicial(${id}, ${documentacionMemoria})`, '-', 'end'))
    );

  }

  createDocumentacionSeguimientoAnual(id: number, documentacionMemoria: IDocumentacionMemoria): Observable<IDocumentacionMemoria> {
    this.logger.debug(MemoriaService.name, `createDocumentacionSeguimientoAnual(${id}, ${documentacionMemoria})`, '-', 'start');
    return this.http.post<IDocumentacionMemoria>(`${this.endpointUrl}/${id}/documentacion-seguimiento-anual`, documentacionMemoria).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `createDocumentacionSeguimientoAnual(${id}, ${documentacionMemoria})`, '-', 'end'))
    );

  }

  updateDocumentacion(
    id: number, documentacionMemoria: IDocumentacionMemoria, idDocumentacionMemoria: number): Observable<IDocumentacionMemoria> {
    this.logger.debug(MemoriaService.name, `updateDocumentacion(${id}, ${documentacionMemoria}, ${idDocumentacionMemoria})`, '-', 'start');
    return this.http.put<IDocumentacionMemoria>
      (`${this.endpointUrl}/${id}/documentacion-inicial/${idDocumentacionMemoria}`, documentacionMemoria).pipe(
        tap(() =>
          this.logger.debug(MemoriaService.name, `updateDocumentacion(${id}, ${documentacionMemoria}, ${idDocumentacionMemoria})`, '-', 'end'))
      );
  }

  /**
   * Devuelve todos las evaluaciones de una memoria id.
   *
   * @param memoriaId id memoria.
   */
  getEvaluacionesMemoria(memoriaId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEvaluacion>> {
    this.logger.debug(MemoriaService.name, `getEvaluacionesMemoria(${memoriaId}, ${options ? JSON.stringify(options) : options})`, '-', 'start');
    return this.find<IEvaluacion, IEvaluacion>(`${this.endpointUrl}/${memoriaId}/evaluaciones`, options).pipe(
      tap(() => this.logger.debug(MemoriaService.name, `getEvaluacionesMemoria(${memoriaId}, ${options ? JSON.stringify(options) : options})`, '-', 'end'))
    );
  }

}
