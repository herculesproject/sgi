import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRelacionEjecucionEconomica } from '@core/models/csp/relacion-ejecucion-economica';
import { environment } from '@env';
import { RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@herculesproject/framework/http';
import { Observable, zip } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaResponse } from './relacion-ejecucion-economica-response';
import { RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER } from './relacion-ejecucion-economica-response-converter';

@Injectable({
  providedIn: 'root'
})
export class RelacionEjecucionEconomicaService extends SgiRestBaseService {
  private static readonly MAPPING = '/relaciones-ejecucion-economica';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${RelacionEjecucionEconomicaService.MAPPING}`,
      http
    );
  }

  findRelacionesGrupos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IRelacionEjecucionEconomica>> {
    return this.find<IRelacionEjecucionEconomicaResponse, IRelacionEjecucionEconomica>(
      `${this.endpointUrl}/grupos`,
      options,
      RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER
    );
  }

  findRelacionesProyectos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IRelacionEjecucionEconomica>> {
    return this.find<IRelacionEjecucionEconomicaResponse, IRelacionEjecucionEconomica>(
      `${this.endpointUrl}/proyectos`,
      options,
      RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve las relaciones de ejecución económica de los grupos en los que el usuario figura como responsable.
   *
   * @param onlyWithParticipacionActual si solo se incluyen los grupos con participación vigente en la fecha actual.
   * @param options opciones de búsqueda.
   */
  findRelacionesGruposInvestigador(
    onlyWithParticipacionActual: boolean,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<IRelacionEjecucionEconomica>> {
    return this.find<IRelacionEjecucionEconomicaResponse, IRelacionEjecucionEconomica>(
      `${this.endpointUrl}/investigador/grupos?onlyWithParticipacionActual=${onlyWithParticipacionActual}`,
      options,
      RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve las relaciones de ejecución económica de los proyectos en los que el usuario.
   * figura como investigador principal.
   *
   * @param onlyWithParticipacionActual si solo se incluyen los proyectos con participación vigente en la fecha actual.
   * @param options opciones de búsqueda.
   */
  findRelacionesProyectosInvestigador(
    onlyWithParticipacionActual: boolean,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<IRelacionEjecucionEconomica>> {
    return this.find<IRelacionEjecucionEconomicaResponse, IRelacionEjecucionEconomica>(
      `${this.endpointUrl}/investigador/proyectos?onlyWithParticipacionActual=${onlyWithParticipacionActual}`,
      options,
      RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER
    );
  }

  findRelacionesProyectoSgeRef(proyectoSgeRef: string): Observable<IRelacionEjecucionEconomica[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.EQUALS, proyectoSgeRef)
    };

    return zip(
      this.findRelacionesGrupos(options).pipe(
        map(result => result.items)
      ),
      this.findRelacionesProyectos(options).pipe(
        map(result => result.items)
      )
    ).pipe(
      map(result => result.reduce((acc, val) => acc.concat(val), []))
    );
  }

  /**
   * Devuelve las relaciones (proyectos y grupos) asociadas a un proyectoSgeRef para el perfil de
   * investigador. El backend comprueba que el usuario tiene acceso a esa referencia (es IP/responsable).
   *
   * @param proyectoSgeRef identificador del proyecto en el SGE.
   */
  findRelacionesProyectoSgeRefInvestigador(proyectoSgeRef: string): Observable<IRelacionEjecucionEconomica[]> {
    return this.http.get<IRelacionEjecucionEconomicaResponse[]>(
      `${this.endpointUrl}/investigador/proyecto-sge-ref/${proyectoSgeRef}`
    ).pipe(
      map(response => (response ?? []).map(item => RELACION_EJECUCION_ECONOMICA_RESPONSE_CONVERTER.toTarget(item)))
    );
  }

}
