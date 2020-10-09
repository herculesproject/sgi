import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { IModeloEjecucion } from '@core/models/csp/modelo-ejecucion';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { of, Observable } from 'rxjs';
import { IFinalidad } from '@core/models/csp/finalidad';
import { ITipoPeriodoJustificacion } from '@core/models/csp/tipo-periodo-justificacion';
import { ITipoPlazosFases } from '@core/models/csp/tipo-plazos-fases';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';



const modelosEjecucion: IModeloEjecucion[] = [
  {
    id: 1, nombre: 'Ayudas y subvenciones'
  },
  {
    id: 2, nombre: 'Contratos'
  },
  {
    id: 3, nombre: 'Convenios'
  }

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
  }

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
  private static readonly MAPPING = '/modelo-ejecucion';

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

}
