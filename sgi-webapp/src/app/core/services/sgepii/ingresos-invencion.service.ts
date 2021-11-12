import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRelacion, TipoEntidad } from '@core/models/rel/relacion';
import { IColumna } from '@core/models/sgepii/columna';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { from, Observable } from 'rxjs';
import { map, mergeMap, reduce, switchMap } from 'rxjs/operators';
import { ProyectoService } from '../csp/proyecto.service';
import { RelacionService } from '../rel/relaciones/relacion.service';

@Injectable({
  providedIn: 'root'
})
export class IngresosInvencionService extends SgiRestBaseService {
  private static readonly MAPPING = '/ingresos-invencion';

  constructor(
    protected http: HttpClient,
    private readonly relacionService: RelacionService,
    private readonly proyectoService: ProyectoService) {
    super(
      `${environment.serviceServers.sgepii}${IngresosInvencionService.MAPPING}`,
      http
    );
  }

  /**
   * Obtiene los Ingresos asociados al ProyectoSGE.
   * 
   * @param proyectoSgeId Id del ProyectoSGE asociado al Proyecto de CSP.
   * @returns Lista de Ingresos asociados al ProyectoSGE.
   */
  getIngresos(proyectoSgeId: string): Observable<IDatoEconomico[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoSgeId);
    const options: SgiRestFindOptions = {
      filter
    };
    return this.find<IDatoEconomico, IDatoEconomico>(
      `${this.endpointUrl}`,
      options
    ).pipe(
      map(response => response.items)
    );
  }


  /**
   * Obtiene la metainformación sobre las columnas dinámicas del Ingreso.
   * 
   * @param proyectoId Id del proyecto asociado a la Invención.
   * @returns Lista de las columnas.
   */
  getColumnas(proyectoId?: string): Observable<IColumna[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoId);
    const options: SgiRestFindOptions = {
      filter
    };
    return this.find<IColumna, IColumna>(
      `${this.endpointUrl}/columnas`,
      options
    ).pipe(
      map(response => response.items)
    );
  }

  /**
   * Obtiene los Ingresos asociados a la Invencion
   * @param invencionId id de la Inencion
   * @returns Lista de Ingresos asociados a la Invención.
   */
  getIngresosByInvencionId(invencionId: number): Observable<IDatoEconomico[]> {
    return this.relacionService.findInvencionRelaciones(invencionId).pipe(
      map(relaciones => this.convertRelacionesToArrayProyectoIds(relaciones)),
      switchMap(proyectoIds => this.getProyectosSgeId(proyectoIds)),
      switchMap(proyectoSgeIds => this.getIngresosProyectosSge(proyectoSgeIds)));
  }

  private convertRelacionesToArrayProyectoIds(relaciones: IRelacion[]): number[] {
    return relaciones.map(relacion => this.getProyectoIdFromRelacion(relacion));
  }

  private getProyectoIdFromRelacion(relacion: IRelacion): number {
    return relacion.tipoEntidadOrigen === TipoEntidad.PROYECTO ? +relacion.entidadOrigen.id : +relacion.entidadDestino.id;
  }

  private getProyectosSgeId(proyectoIds: number[]): Observable<string[]> {
    return from(proyectoIds).pipe(
      mergeMap(proyectoId => this.getProyectoSgeId(proyectoId)),
      // flat array
      reduce((acc, val) => acc.concat(val), [])
    );
  }

  private getProyectoSgeId(proyectoId: number): Observable<string[]> {
    return this.proyectoService.findAllProyectosSgeProyecto(proyectoId).pipe(
      map(({ items }) => items.map(proyectoSge => proyectoSge.proyectoSge.id))
    );
  }

  private getIngresosProyectosSge(proyectoSgeIds: string[]): Observable<IDatoEconomico[]> {
    return from(proyectoSgeIds).pipe(
      mergeMap(proyectoSgeId => this.getIngresosProyectoSge(proyectoSgeId)),
      // flat array
      reduce((acc, val) => acc.concat(val), [])
    );
  }

  private getIngresosProyectoSge(proyectoSgeId: string): Observable<IDatoEconomico[]> {
    return this.getIngresos(proyectoSgeId);
  }
}
