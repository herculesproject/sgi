import { Injectable } from '@angular/core';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IRelacionEjecucionEconomica, TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, reduce, switchMap } from 'rxjs/operators';

/**
 * Anualidades de los proyectos relacionados con la ejecución económica y su origen
 * (presupuesto del proyecto o fechas del proyecto).
 */
export interface IEjecucionEconomicaAnualidades {
  anualidades: string[];
  hasAnualidadesPresupuesto: boolean;
  hasAnualidadesFechas: boolean;
}

/**
 * Obtención de las anualidades con las que se consultan los datos económicos de la ejecución económica.
 */
@Injectable({
  providedIn: 'root'
})
export class EjecucionEconomicaAnualidadesService {

  constructor(
    private readonly proyectoAnualidadService: ProyectoAnualidadService,
    private readonly proyectoService: ProyectoService
  ) { }

  /**
   * Obtiene las anualidades asociadas a los proyectos relacionados,
   * si los proyectos no tienen presupuesto por anualidades y anualidadesRequired
   * es true se recuperan las anualidades de las fechas del proyecto
   *
   * @param relaciones relaciones de la ejecución económica
   * @param anualidadesRequired si las anualidades son obligatorias
   * @returns las anualidades de los proyectos relacionados y si se han obtenido del presupuesto o de las fechas del proyecto
   */
  getAnualidadesProyectos(
    relaciones: IRelacionEjecucionEconomica[],
    anualidadesRequired: boolean
  ): Observable<IEjecucionEconomicaAnualidades> {
    const proyectoIds: string[] = relaciones
      .filter(relacion => relacion.tipoEntidad === TipoEntidad.PROYECTO)
      .map(relacion => relacion.id.toString());

    if (proyectoIds.length === 0) {
      return of({
        anualidades: [],
        hasAnualidadesFechas: false,
        hasAnualidadesPresupuesto: false
      });
    }

    return this.proyectoAnualidadService.findAllByProyectoIdIn(proyectoIds).pipe(
      // Las anualidades genéricas no tiene año y no aporta anualidades
      map(response => response.items.filter(anualidad => anualidad.anio)),
      switchMap((proyectoAnualidadesPresupuesto: IProyectoAnualidad[]) => {

        const proyectosConAnualidades = new Set<string>(
          proyectoAnualidadesPresupuesto
            .filter(anualidad => anualidad.proyecto?.id)
            .map(anualidad => anualidad.proyecto.id.toString())
        );

        const proyectosSinAnualidades = proyectoIds.filter(id => !proyectosConAnualidades.has(id));

        if (proyectosSinAnualidades.length === 0 || !anualidadesRequired) {
          return of({
            anualidades: [... new Set(proyectoAnualidadesPresupuesto.map(anualidad => anualidad.anio.toString()))],
            hasAnualidadesFechas: false,
            hasAnualidadesPresupuesto: !!proyectoAnualidadesPresupuesto.length
          });
        }

        return from(proyectosSinAnualidades).pipe(
          mergeMap(proyectoId =>
            this.proyectoService.getAnualidadesProyecto(Number(proyectoId)).pipe(
              map(anualidades => anualidades.map(anualidad => ({
                anio: Number(anualidad),
                proyecto: { id: Number(proyectoId) } as IProyecto,
              } as IProyectoAnualidad)))
            )
          ),
          reduce<IProyectoAnualidad[], IProyectoAnualidad[]>(
            (acc, proyectoAnualidadesFechas) => [...acc, ...proyectoAnualidadesFechas],
            []
          ),
          map((proyectoAnualidadesFechas: IProyectoAnualidad[]) => {
            const proyectoAnualidades = [
              ...proyectoAnualidadesPresupuesto,
              ...proyectoAnualidadesFechas
            ];

            return {
              anualidades: [... new Set(
                proyectoAnualidades
                  .map(anualidad => anualidad.anio.toString())
                  .sort((a, b) => a.localeCompare(b))
              )],
              hasAnualidadesFechas: !!proyectoAnualidadesFechas?.length,
              hasAnualidadesPresupuesto: !!proyectoAnualidadesPresupuesto.length
            };
          })
        );
      })
    );
  }

  /**
   * Obtiene las anualidades que se usan por defecto cuando las anualidades son obligatorias,
   * si las anualidades se han obtenido de las fechas del proyecto solo se usan
   * las anteriores o iguales al año actual
   *
   * @param ejecucionEconomicaAnualidades anualidades de los proyectos relacionados
   * @returns lista de anualidades por defecto
   */
  getDefaultAnualidades(ejecucionEconomicaAnualidades: IEjecucionEconomicaAnualidades): string[] {
    if (!ejecucionEconomicaAnualidades.hasAnualidadesPresupuesto && ejecucionEconomicaAnualidades.hasAnualidadesFechas) {
      const currentYear = new Date().getFullYear();
      return ejecucionEconomicaAnualidades.anualidades?.filter(anio => Number(anio) <= currentYear);
    }

    return ejecucionEconomicaAnualidades.anualidades;
  }

}
