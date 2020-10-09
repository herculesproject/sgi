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
import { IEnlace } from '@core/models/csp/enlace';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';


const convocatorias: IConvocatoria[] = [
  {
    id: 1, referencia: 'REF001', titulo: 'Ayudas plan propio', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'Universidad', planInvestigacion: 'Plan propio',
    entidadFinanciadora: 'Universidad', fuenteFinanciacion: 'Fondos propios', activo: true,
    estado: 'Borrador', unidadGestion: { id: 1, nombre: 'OTRI' }, anio: 2020,
    modeloEjecucion: { id: 2, nombre: 'Contratos' } as IModeloEjecucion, finalidad: { id: 3, nombre: 'Servicios Técnicos' },
    duracionMeses: 20, ambitoGeografico: { id: 3, nombre: 'Autonómico' }, clasificacionProduccion: 'Proyectos competitivos',
    regimenConcurrencia: { id: 2, nombre: 'Concurrencia competitiva' },
    proyectoColaborativo: 'Sí', destinatarios: 'Equipo de proyecto', entidadGestora: '',
    descripcionConvocatoria: 'Plan fondos propios de Universidad', observaciones: ''
  },
  {
    id: 2, referencia: 'REF002', titulo: 'Programa 2020', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'AEI', planInvestigacion: 'Plan Nacional 2020',
    entidadFinanciadora: 'AEI', fuenteFinanciacion: 'Presupuestos generados estado', activo: false,
    estado: 'Borrador', unidadGestion: { id: 1, nombre: 'OTRI' }, anio: 2019,
    modeloEjecucion: { id: 2, nombre: 'Contratos' } as IModeloEjecucion, finalidad: { id: 2, nombre: 'Contratación RRHH' },
    duracionMeses: 12, ambitoGeografico: { id: 2, nombre: 'Local' }, clasificacionProduccion: 'Contratos, convenios  y proyectos no competitivos',
    regimenConcurrencia: { id: 1, nombre: 'Contratación RRHH' },
    proyectoColaborativo: 'No', destinatarios: 'Grupo de investigación', entidadGestora: '',
    descripcionConvocatoria: '', observaciones: 'Contratación 2019'
  },
  {
    id: 3, referencia: 'REF003', titulo: 'Fondo COVID', fechaInicioSolicitud: new Date(),
    fechaFinSolicitud: new Date(), estadoConvocante: 'CRUE', planInvestigacion: 'Plan COVID',
    entidadFinanciadora: 'CSIC', fuenteFinanciacion: 'Fondos COVID', activo: true,
    estado: 'Borrador', unidadGestion: { id: 2, nombre: 'OPE' }, anio: 2020,
    modeloEjecucion: { id: 1, nombre: 'Ayudas y subvenciones' } as IModeloEjecucion, finalidad: { id: 3, nombre: 'Proyectos I+D' },
    duracionMeses: 24, ambitoGeografico: { id: 5, nombre: 'Europeo' }, clasificacionProduccion: 'Proyectos competitivos',
    regimenConcurrencia: { id: 2, nombre: 'Concurrencia competitiva' },
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

const entidadesFinanciadoras: IEntidadFinanciadora[] = [
  {
    id: 1,
    nombre: 'Entidad 1',
    cif: 'V2556493',
    fuenteFinanciacion: 'PGE',
    ambito: 'Nacional',
    tipoFinanciacion: 'Subvención',
    porcentajeFinanciacion: '80'
  } as IEntidadFinanciadora,
  {
    id: 2,
    nombre: 'Entidad 2',
    cif: 'J8253179',
    fuenteFinanciacion: 'Fondos propios 2020',
    ambito: 'Propio',
    tipoFinanciacion: 'Subvención',
    porcentajeFinanciacion: '20'
  } as IEntidadFinanciadora,
];

const enlaces: IEnlace[] = [
  {
    id: 1, url: 'www.boe.es', descripcion: 'Publicación BOE', tipoEnlace: 'BOE', activo: true
  },
  {
    id: 2, url: 'www.solicitamineco.es', descripcion: 'URL de convocatoria', tipoEnlace: 'Publicación convocatoria', activo: true
  }

];


const areasTematicas: IAreaTematica[] = [
  {
    id: 1, listadoArea: 'AEI', area: 'Cáncer', observaciones: ' Estudio del impacto genético'
  },
  {
    id: 2, listadoArea: 'DPT', area: 'Inmunidad, infección  y nuevas terapias', observaciones: 'Estudio del impacto genético'
  }

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
   * Recupera listado mock de enlaces.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  getEnlaces(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEnlace>> {
    this.logger.debug(ConvocatoriaService.name, `getEnlaces(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    // return this.find<IEntidadesConvocantes, IEntidadesConvocantes>(`${this.endpointUrl}/${id}/comentarios`, options)
    const list = {
      page: null,
      total: enlaces.length,
      items: enlaces
    } as SgiRestListResult<IEnlace>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `getEnlaces(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
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

  /*
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
      `getEntidadesConvocantes(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const list = {
      page: null,
      total: entidadesConvocantes.length,
      items: entidadesConvocantes
    } as SgiRestListResult<IEntidadConvocante>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `getEntidadesConvocantes(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }
  getSeguimientosCientificos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISeguimientoCientifico>> {
    this.logger.debug(ConvocatoriaService.name,
      `getSeguimientosCientificos(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const list = {
      page: null,
      total: seguimientosCientificos.length,
      items: seguimientosCientificos
    } as SgiRestListResult<ISeguimientoCientifico>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `getSeguimientosCientificos(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }

  getEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEntidadFinanciadora>> {
    this.logger.debug(ConvocatoriaService.name,
      `getEntidadesFinanciadores(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'start');
    const list = {
      page: null,
      total: entidadesFinanciadoras.length,
      items: entidadesFinanciadoras
    } as SgiRestListResult<IEntidadFinanciadora>;
    return of(list)
      .pipe(
        tap(() => this.logger.debug(ConvocatoriaService.name,
          `getEntidadesFinanciadoras(${id}, ${options ? JSON.stringify(options) : options}`, '-', 'end'))
      );
  }


  /**
   * Recupera listado mock de modelos de áreas temáticas.
   * @param idConvocatoria opciones de búsqueda.
   * @returns listado de modelos de áreas temáticas.
   */
  findAreaTematica(idConvocatoria: number): Observable<SgiRestListResult<IAreaTematica>> {
    this.logger.debug(ConvocatoriaService.name, `findAreaTematica(idConvocatoria)`, '-', 'START');

    return of({
      page: null,
      total: areasTematicas.length,
      items: areasTematicas
    } as SgiRestListResult<IAreaTematica>);
  }

  create(convocatoria: IConvocatoria): Observable<IConvocatoria> {
    return of(convocatoria);
  }

  update(idConvocatoria: number, convocatoria: IConvocatoria): Observable<IConvocatoria> {
    return of(convocatoria);
  }

}
