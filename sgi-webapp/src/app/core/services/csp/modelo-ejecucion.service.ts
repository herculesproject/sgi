import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { IModeloEjecucion } from '@core/models/csp/modelo-ejecucion';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { of, Observable } from 'rxjs';
import { IFinalidad } from '@core/models/csp/finalidad';



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

@Injectable({
  providedIn: 'root'
})
export class ModeloEjecucionService extends SgiRestService<number, IModeloEjecucion> {
  private static readonly MAPPING = '/modelo-ejecucion';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ModeloEjecucionService.name,
      logger,
      `${environment.serviceServers.eti}${ModeloEjecucionService.MAPPING}`,
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
}
