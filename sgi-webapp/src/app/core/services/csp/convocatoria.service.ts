import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { of, Observable } from 'rxjs';
import { IPlazosFases } from '@core/models/csp/plazos-fases';
import { ISeguimientoCientifico } from '@core/models/csp/seguimiento-cientifico';
import { tap } from 'rxjs/operators';
import { IEntidadConvocante } from '@core/models/csp/entidad-convocante';
import { DateUtils } from '@core/utils/date-utils';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';

const entidadesConvocantes: IEntidadConvocante[] = [
  {
    id: 1,
    nombre: 'Entidad 1',
    cif: 'V5920978',
    itemPrograma: 'Modalidad K',
    plan: 'Plan Nacional 2020-2023',
    programa: 'Programa 1'
  } as IEntidadConvocante,
  {
    id: 2,
    nombre: 'Entidad 2',
    cif: 'V3142340',
    itemPrograma: 'Predoctorales',
    plan: 'Plan propio',
    programa: 'Programa ayudas propias'
  } as IEntidadConvocante
];

const seguimientosCientificos: ISeguimientoCientifico[] = [
  {
    numPeriodo: 1,
    mesInicial: 1,
    mesFinal: 18,
    fechaInicio: DateUtils.fechaToDate('05/15/2020'),
    fechaFin: DateUtils.fechaToDate('06/16/2020'),
    observaciones: 'Primer periodo de seguimiento'
  } as ISeguimientoCientifico,
  {
    numPeriodo: 2,
    mesInicial: 19,
    mesFinal: 36,
    fechaInicio: DateUtils.fechaToDate('05/20/2020'),
    fechaFin: DateUtils.fechaToDate('06/20/2020'),
    observaciones: 'Segundo periodo de seguimiento'
  } as ISeguimientoCientifico,
];

@Injectable({
  providedIn: 'root'
})

export class ConvocatoriaService extends SgiRestService<number, IConvocatoria> {
  private static readonly MAPPING = '/convocatorias';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ConvocatoriaService.name,
      logger,
      `${environment.serviceServers.csp}${ConvocatoriaService.MAPPING}`,
      http
    );
  }

  findAllTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoria>> {
    this.logger.debug(ConvocatoriaService.name, `${this.findAllTodos.name}(`, '-', 'START');
    return this.find<IConvocatoria, IConvocatoria>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(ConvocatoriaService.name, `${this.findAllTodos.name}()`, '-', 'END'))
    );
  }

  /**
   * Recupera listado de periodos justificacion de una convocatoria.
   *
   * @param id Id de la convocatoria.
   * @param options opciones de búsqueda.
   * @returns periodos de justificacion de la convocatoria.
   */
  getPeriodosJustificacion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaPeriodoJustificacion>> {
    this.logger.debug(ConvocatoriaService.name, `getPeriodosJustificacion(${id}, ${options ? JSON.stringify(options) : options})`, '-', 'START');
    return this.find<IConvocatoriaPeriodoJustificacion, IConvocatoriaPeriodoJustificacion>(`${this.endpointUrl}/${id}/convocatoriaperiodojustificaciones`, options)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name, `getPeriodosJustificacion(${id}, ${options ? JSON.stringify(options) : options})`, '-', 'END'))
      );
  }

  /**
   * Recupera listado de enlaces.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  getEnlaces(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEnlace>> {
    this.logger.debug(ConvocatoriaService.name, `getEnlaces(${id}, ${options})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${id}/convocatoriaenlaces`;
    return this.find<IConvocatoriaEnlace, IConvocatoriaEnlace>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name, `getEnlaces(${id}, ${options})`, '-', 'end'))
      );
  }

  /**
   * Recupera listado de plazos y fases de una convocatoria
   * @param idConvocatoria convocatoria
   * @param options opciones de búsqueda.
   */
  findFasesConvocatoria(idConvocatoria: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaFase>> {
    this.logger.debug(ConvocatoriaService.name, `findFasesConvocatoria(${idConvocatoria}, ${options})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idConvocatoria}/convocatoriafases`;
    return this.find<IConvocatoriaFase, IConvocatoriaFase>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name, `findFasesConvocatoria(${idConvocatoria}, ${options})`, '-', 'end'))
      );
  }

  /**
   * Recupera los hitos de una convocatoria
   * @param idConvocatoria Identificador de la convocatoria.
   * @returns Listado de hitos.
   */
  findHitosConvocatoria(idConvocatoria: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaHito>> {
    this.logger.debug(ConvocatoriaService.name, `findHitosConvocatoria(${idConvocatoria}, ${options})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idConvocatoria}/convocatoriahitos`;
    return this.find<IConvocatoriaHito, IConvocatoriaHito>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name, `findHitosConvocatoria(${idConvocatoria}, ${options})`, '-', 'end'))
      );
  }

  /**
   * Recupera las entidades convocantes.
   *
   * @param id Id de la convocatoria
   * @param options opciones de búsqueda.
   */
  getEntidadesConvocantes(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEntidadConvocante>> {
    this.logger.debug(ConvocatoriaService.name,
      `${this.getEntidadesConvocantes.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const list = {
      page: null,
      total: entidadesConvocantes.length,
      items: entidadesConvocantes
    } as SgiRestListResult<IEntidadConvocante>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `${this.getEntidadesConvocantes.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  getSeguimientosCientificos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISeguimientoCientifico>> {
    this.logger.debug(ConvocatoriaService.name,
      `${this.getSeguimientosCientificos.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const list = {
      page: null,
      total: seguimientosCientificos.length,
      items: seguimientosCientificos
    } as SgiRestListResult<ISeguimientoCientifico>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `${this.getSeguimientosCientificos.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  findEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadFinanciadora>> {
    this.logger.debug(ConvocatoriaService.name,
      `${this.findEntidadesFinanciadoras.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IConvocatoriaEntidadFinanciadora, IConvocatoriaEntidadFinanciadora>(
      `${this.endpointUrl}/${id}/convocatoriaentidadfinanciadoras`, options).pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `${this.findEntidadesFinanciadoras.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  findAllConvocatoriaFases(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaFase>> {
    this.logger.debug(ConvocatoriaService.name,
      `${this.findAllConvocatoriaFases.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IConvocatoriaFase, IConvocatoriaFase>(
      `${this.endpointUrl}/${id}/convocatoriafases`, options).pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `${this.findAllConvocatoriaFases.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  findAllConvocatoriaEntidadConvocantes(id: number, options?:
    SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadConvocante>> {
    this.logger.debug(ConvocatoriaService.name,
      `${this.findAllConvocatoriaFases.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IConvocatoriaEntidadConvocante, IConvocatoriaEntidadConvocante>(
      `${this.endpointUrl}/${id}/convocatoriaentidadconvocantes`, options).pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `${this.findAllConvocatoriaFases.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  findAllConvocatoriaEntidadGestora(id: number, options?:
    SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadConvocante>> {
    this.logger.debug(ConvocatoriaService.name,
      `${this.findAllConvocatoriaFases.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IConvocatoriaEntidadConvocante, IConvocatoriaEntidadConvocante>(
      `${this.endpointUrl}/${id}/convocatoriaentidadgestoras`, options).pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `${this.findAllConvocatoriaFases.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  /**
   * Recupera listado mock de modelos de áreas temáticas.
   * @param idConvocatoria opciones de búsqueda.
   * @returns listado de modelos de áreas temáticas.
   */
  findAreaTematicas(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaAreaTematica>> {
    this.logger.debug(ConvocatoriaService.name, `${this.findAreaTematicas.name}(id: ${id})`, '-', 'START');
    return this.find<IConvocatoriaAreaTematica, IConvocatoriaAreaTematica>(
      `${this.endpointUrl}/${id}/convocatoriaareatematicas`, options).pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name, `${this.findAreaTematicas.name}(id: ${id})`, '-', 'END'))
      );
  }
}
