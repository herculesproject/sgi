import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IPeriodoJustificacion } from '@core/models/csp/periodo-justificacion';
import { of, Observable } from 'rxjs';
import { IPlazosFases } from '@core/models/csp/plazos-fases';
import { ISeguimientoCientifico } from '@core/models/csp/seguimiento-cientifico';
import { tap } from 'rxjs/operators';
import { IHito } from '@core/models/csp/hito';
import { IEntidadConvocante } from '@core/models/csp/entidad-convocante';
import { DateUtils } from '@core/utils/date-utils';
import { IModeloEjecucion, ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { ITipoRegimenConcurrencia } from '@core/models/csp/tipo-regimen-concurrencia';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';

const convocatorias: IConvocatoria[] = [
  {
    id: 1, referencia: 'REF001', titulo: 'Ayudas plan propio', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'Universidad', planInvestigacion: 'Plan propio',
    entidadFinanciadora: 'Universidad', fuenteFinanciacion: 'Fondos propios', activo: true,
    estado: 'Borrador', unidadGestion: { id: 1, nombre: 'OTRI' } as IUnidadGestion, anio: 2020,
    modeloEjecucion: { id: 2, nombre: 'Contratos' } as IModeloEjecucion,
    finalidad: { id: 3, nombre: 'Servicios Técnicos' } as ITipoFinalidad,
    duracionMeses: 20, tipoAmbitoGeografico: { id: 3, nombre: 'Autonómico' } as ITipoAmbitoGeografico, clasificacionProduccion: 'Proyectos competitivos',
    tipoRegimenConcurrencia: { id: 2, nombre: 'Concurrencia competitiva' } as ITipoRegimenConcurrencia,
    proyectoColaborativo: 'Sí', destinatarios: 'Equipo de proyecto', entidadGestora: '',
    descripcionConvocatoria: 'Plan fondos propios de Universidad', observaciones: ''
  },
  {
    id: 2, referencia: 'REF002', titulo: 'Programa 2020', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'AEI', planInvestigacion: 'Plan Nacional 2020',
    entidadFinanciadora: 'AEI', fuenteFinanciacion: 'Presupuestos generados estado', activo: false,
    estado: 'Borrador', unidadGestion: { id: 1, nombre: 'OTRI' } as IUnidadGestion, anio: 2019,
    modeloEjecucion: { id: 2, nombre: 'Contratos' } as IModeloEjecucion,
    finalidad: { id: 2, nombre: 'Contratación RRHH' } as ITipoFinalidad,
    duracionMeses: 12, tipoAmbitoGeografico: { id: 2, nombre: 'Local' } as ITipoAmbitoGeografico, clasificacionProduccion: 'Contratos, convenios  y proyectos no competitivos',
    tipoRegimenConcurrencia: { id: 1, nombre: 'Contratación RRHH' } as ITipoRegimenConcurrencia,
    proyectoColaborativo: 'No', destinatarios: 'Grupo de investigación', entidadGestora: '',
    descripcionConvocatoria: '', observaciones: 'Contratación 2019'
  },
  {
    id: 3, referencia: 'REF003', titulo: 'Fondo COVID', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'CRUE', planInvestigacion: 'Plan COVID',
    entidadFinanciadora: 'CSIC', fuenteFinanciacion: 'Fondos COVID', activo: true,
    estado: 'Borrador', unidadGestion: { id: 2, nombre: 'OPE' } as IUnidadGestion, anio: 2020,
    modeloEjecucion: { id: 1, nombre: 'Ayudas y subvenciones' } as IModeloEjecucion,
    finalidad: { id: 3, nombre: 'Proyectos I+D' } as ITipoFinalidad,
    duracionMeses: 24, tipoAmbitoGeografico: { id: 5, nombre: 'Europeo' } as ITipoAmbitoGeografico, clasificacionProduccion: 'Proyectos competitivos',
    tipoRegimenConcurrencia: { id: 2, nombre: 'Concurrencia competitiva' } as ITipoRegimenConcurrencia,
    proyectoColaborativo: 'Sí', destinatarios: 'Individual', entidadGestora: '',
    descripcionConvocatoria: 'Convocatoria Proyecto Covid I+D', observaciones: ''
  }

];

const periodosJustificacion: IPeriodoJustificacion[] = [
  {
    id: 1, numPeriodo: 1, tipoJustificacion: { id: 1, nombre: 'Periódica' }, mesInicial: 'Enero', mesFinal: 'Marzo',
    fechaInicio: new Date(), fechaFin: new Date(), observaciones: 'Primer periodo de justificación', activo: true
  },
  {
    id: 2, numPeriodo: 2, tipoJustificacion: { id: 2, nombre: 'Periódica' }, mesInicial: 'Septiembre', mesFinal: 'Diciembre',
    fechaInicio: new Date(), fechaFin: new Date(), observaciones: 'Segundo periodo de justificación', activo: true

  }];

const hitos: IHito[] = [
  {
    id: 1, fechaInicio: new Date(), tipoHito: {
      id: 1, nombre: 'Resolución interna',
      descripcion: '', activo: false
    }, comentario: '', aviso: true
  },
  {
    id: 1, fechaInicio: new Date(), tipoHito: {
      id: 2, nombre: 'Resolución definitiva',
      descripcion: '', activo: false
    }, comentario: '', aviso: false
  }
];

const plazosFases: IPlazosFases[] = [
  {
    id: 1, fechaInicio: new Date(), fechaFin: new Date(), tipoPlazosFases: { id: 1, nombre: 'Presentación interna solicitudes' },
    observaciones: 'Recogida de solicitudes en UGI', activo: true
  },
  {
    id: 1, fechaInicio: new Date(), fechaFin: new Date(), tipoPlazosFases: { id: 2, nombre: 'Presentación solicitudes' },
    observaciones: 'Entrega de solicitudes en Ministerio', activo: true
  }
];


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

  /**
   * Recupera listado mock de convocatorias.
   * @param options opciones de búsqueda.
   * @returns listado de convocatorias.
   */
  findConvocatoria(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoria>> {
    this.logger.debug(ConvocatoriaService.name, `findConvocatoria(${options ? JSON.stringify(options) : ''})`, '-', 'START');
    return of({
      page: null,
      total: convocatorias.length,
      items: convocatorias
    } as SgiRestListResult<IConvocatoria>);
  }

  /**
   * Recupera listado mock de periodos justificacion.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  getPeriodosJustificacion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IPeriodoJustificacion>> {
    this.logger.debug(ConvocatoriaService.name, `getPeriodosJustificacion(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const list = {
      page: null,
      total: periodosJustificacion.length,
      items: periodosJustificacion
    } as SgiRestListResult<IPeriodoJustificacion>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `getPeriodosJustificacion(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
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
   * Recupera listado mock de plazos y fases.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  getPlazosFases(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IPlazosFases>> {
    this.logger.debug(ConvocatoriaService.name, `getPlazosFases(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const list = {
      page: null,
      total: plazosFases.length,
      items: plazosFases
    } as SgiRestListResult<IPlazosFases>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `getPlazosFases(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  /**
   * Recupera una convocatoria por id.
   * @param idConvocatoria Identificador de la convocatoria.
   * @return convocatoria.
   */
  findById(idConvocatoria: number) {
    this.logger.debug(ConvocatoriaService.name, `findById(idConvocatoria)`, '-', 'START');
    return of(convocatorias[idConvocatoria - 1]);
  }

  /**
   * Recupera los hitos de una convocatoria
   * @param idConvocatoria Identificador de la convocatoria.
   * @returns Listado de hitos.
   */
  findHitosConvocatoria(idConvocatoria: number): Observable<SgiRestListResult<IHito>> {
    this.logger.debug(ConvocatoriaService.name, `findHitosConvocatoria(idConvocatoria)`, '-', 'START');
    return of({
      page: null,
      total: hitos.length,
      items: hitos
    } as SgiRestListResult<IHito>);
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

  getEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadFinanciadora>> {
    this.logger.debug(ConvocatoriaService.name,
      `${this.getEntidadesFinanciadoras.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    return this.find<IConvocatoriaEntidadFinanciadora, IConvocatoriaEntidadFinanciadora>(
      `${this.endpointUrl}/${id}/convocatoriaentidadfinanciadoras`, options).pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `${this.getEntidadesFinanciadoras.name}(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
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

  create(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    return of(convocatoria);
  }

  update(idConvocatoria: number, convocatoria: IConvocatoria): Observable<IConvocatoria> {
    return of(convocatoria);
  }

}
