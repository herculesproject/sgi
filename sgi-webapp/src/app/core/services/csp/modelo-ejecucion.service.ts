import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { of, Observable } from 'rxjs';
import { IFinalidad } from '@core/models/csp/finalidad';
import { ITipoPeriodoJustificacion } from '@core/models/csp/tipo-periodo-justificacion';
import { ITipoPlazosFases } from '@core/models/csp/tipo-plazos-fases';
import { IModeloEjecucion, ITipoHito } from '@core/models/csp/tipos-configuracion';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { tap } from 'rxjs/operators';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';



const modelosEjecucion: IModeloEjecucion[] = [
  {
    id: 1, nombre: 'Ayudas y subvenciones'
  } as IModeloEjecucion,
  {
    id: 2, nombre: 'Contratos'
  } as IModeloEjecucion,
  {
    id: 3, nombre: 'Convenios'
  } as IModeloEjecucion
];



const finalidades: IFinalidad[] = [
  {
    id: 1, nombre: 'Proyectos I+D'
  },
  {
    id: 2, nombre: 'Contratación RRHH'
  },
  {
    id: 3, nombre: 'Servicios Técnicos'
  },
  {
    id: 4, nombre: 'Infraestructuras'
  } as IFinalidad

];


const tiposHito: ITipoHito[] = [
  {
    id: 1, nombre: 'Resolución interna', descripcion: '', activo: false
  },
  {
    id: 2, nombre: 'Resolución definitiva', descripcion: '', activo: false
  } as ITipoHito

];

const tipoJustificacion: ITipoPeriodoJustificacion[] = [
  {
    id: 1, nombre: 'Periodica'
  },
  {
    id: 2, nombre: 'Final'
  }

];

const tipoPlazoFase: ITipoPlazosFases[] = [
  {
    id: 1, nombre: 'Presentación interna solicitudes'
  },
  {
    id: 2, nombre: 'Presentación solicitudes'
  }

];

@Injectable({
  providedIn: 'root'
})
export class ModeloEjecucionService extends SgiRestService<number, IModeloEjecucion> {
  private static readonly MAPPING = '/modeloejecuciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ModeloEjecucionService.name,
      logger,
      `${environment.serviceServers.csp}${ModeloEjecucionService.MAPPING}`,
      http
    );
  }


  /**
   * Recupera listado mock de modelos de ejecución.
   * @param options opciones de búsqueda.
   * @returns listado de modelos de ejecución.
   */
  findModelosEjecucion(options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloEjecucion>> {
    this.logger.debug(ModeloEjecucionService.name, `findUnidadesGestion(${options ? JSON.stringify(options) : ''})`, '-', 'START');

    return of({
      page: null,
      total: modelosEjecucion.length,
      items: modelosEjecucion
    } as SgiRestListResult<IModeloEjecucion>);
  }


  /**
   * Recupera listado mock de finalidades de un modelo de ejecución.
   * @param idModeloEjecucion Identificador del modelo de ejecución.
   * @returns listado de finalidades.
   */
  findFinalidades(idModeloEjecucion: number): Observable<SgiRestListResult<IFinalidad>> {
    this.logger.debug(ModeloEjecucionService.name, `findFinalidades(idModeloEjecucion)`, '-', 'START');

    return of({
      page: null,
      total: finalidades.length,
      items: finalidades
    } as SgiRestListResult<IFinalidad>);
  }


  /**
   * Recupera los hitos de una convocatoria
   * @param idModeloEjecucion Identificador del modelo de ejecución.
   * @returns Listado de tipos de hitos.
   */
  findTipoHitos(idModeloEjecucion: number): Observable<SgiRestListResult<ITipoHito>> {
    this.logger.debug(ModeloEjecucionService.name, `findTipoHitos(idModeloEjecucion)`, '-', 'START');
    return of({
      page: null,
      total: tiposHito.length,
      items: tiposHito
    } as SgiRestListResult<ITipoHito>);
  }

  /**
   * Recupera los tipos de un tipo de periodo de justificacion
   * @param idModeloEjecucion Identificador del modelo de ejecución.
   * @returns Listado de periodo de justigicacion
   */
  findTipoJustificacion(idModeloEjecucion: number): Observable<SgiRestListResult<ITipoPeriodoJustificacion>> {
    this.logger.debug(ModeloEjecucionService.name, `findTipoJustificacion(idModeloEjecucion)`, '-', 'START');
    return of({
      page: null,
      total: tipoJustificacion.length,
      items: tipoJustificacion
    } as SgiRestListResult<ITipoPeriodoJustificacion>);
  }

  /**
   * Recupera los tipos de una fase de plazo
   * @param idModeloEjecucion Identificador del modelo de ejecución.
   * @returns Listado de una fase de plazo
   */
  findPlazoFase(idModeloEjecucion: number): Observable<SgiRestListResult<ITipoPlazosFases>> {
    this.logger.debug(ModeloEjecucionService.name, `findPlazoFase(idModeloEjecucion)`, '-', 'START');
    return of({
      page: null,
      total: tipoPlazoFase.length,
      items: tipoPlazoFase
    } as SgiRestListResult<ITipoPlazosFases>);
  }

  findModeloTipoEnlace(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoEnlace>> {
    this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoEnlace.name}(id: ${id})`, '-', 'START');
    return this.find<IModeloTipoEnlace, IModeloTipoEnlace>(`${this.endpointUrl}/${id}/modelotipoenlaces`, options).pipe(
      tap(() => this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoEnlace.name}(id: ${id})`, '-', 'END'))
    );
  }

  findModeloTipoFinalidad(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoFinalidad>> {
    this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoFinalidad.name}(id: ${id})`, '-', 'START');
    return this.find<IModeloTipoFinalidad, IModeloTipoFinalidad>(`${this.endpointUrl}/${id}/modelotipofinalidades`, options).pipe(
      tap(() => this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoFinalidad.name}(id: ${id})`, '-', 'END'))
    );
  }

  /**
   * Muestra todos los modelos tipo de fase
   * @param id modelo de ejecucion
   * @param options opciones de búsqueda.
   */
  findModeloTipoFase(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoFase>> {
    this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoFase.name}(id: ${id})`, '-', 'START');
    return this.find<IModeloTipoFase, IModeloTipoFase>(`${this.endpointUrl}/${id}/modelotipofases`, options).pipe(
      tap(() => this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoFase.name}(id: ${id})`, '-', 'END'))
    );
  }

  /**
   * Muestra todos los modelos tipo de documento
   * @param id modelo de ejecucion
   * @param options opciones de búsqueda.
   */
  findModeloTipoDocumento(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoDocumento>> {
    this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoDocumento.name}(id: ${id})`, '-', 'START');
    return this.find<IModeloTipoDocumento, IModeloTipoDocumento>(`${this.endpointUrl}/${id}/modelotipodocumentos`, options).pipe(
      tap(() => this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoDocumento.name}(id: ${id})`, '-', 'END'))
    );
  }

  findModeloTipoHito(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoHito>> {
    this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoHito.name}(id: ${id})`, '-', 'START');
    return this.find<IModeloTipoHito, IModeloTipoHito>(`${this.endpointUrl}/${id}/modelotipohitos`, options).pipe(
      tap(() => this.logger.debug(ModeloEjecucionService.name, `${this.findModeloTipoHito.name}(id: ${id})`, '-', 'END'))
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloEjecucion>> {
    this.logger.debug(ModeloEjecucionService.name, `${this.findTodos.name}(`, '-', 'START');
    return this.find<IModeloEjecucion, IModeloEjecucion>(`${this.endpointUrl}/todos`, options).pipe(
      tap(() => this.logger.debug(ModeloEjecucionService.name, `${this.findTodos.name}()`, '-', 'END'))
    );
  }

}
