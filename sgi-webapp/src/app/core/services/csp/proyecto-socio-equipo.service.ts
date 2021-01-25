import { Injectable } from '@angular/core';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { SgiMutableRestService } from '@sgi/framework/http';
import { IProyectoSocioEquipo } from '@core/models/csp/proyecto-socio-equipo';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPersona } from '@core/models/sgp/persona';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface IProyectoSocioEquipoBackend {
  id: number;
  proyectoSocio: IProyectoSocio;
  rolProyecto: IRolProyecto;
  personaRef: string;
  fechaInicio: Date;
  fechaFin: Date;
}

@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioEquipoService extends
  SgiMutableRestService<number, IProyectoSocioEquipoBackend, IProyectoSocioEquipo> {
  private static readonly MAPPING = '/proyectosocioequipos';
  static CONVERTER = new class extends SgiBaseConverter<IProyectoSocioEquipoBackend, IProyectoSocioEquipo> {
    toTarget(value: IProyectoSocioEquipoBackend): IProyectoSocioEquipo {
      const persona: IPersona = {
        identificadorLetra: undefined,
        identificadorNumero: undefined,
        nivelAcademico: undefined,
        nombre: undefined,
        personaRef: value.personaRef,
        primerApellido: undefined,
        segundoApellido: undefined,
        vinculacion: undefined
      };
      const result: IProyectoSocioEquipo = {
        id: value.id,
        fechaFin: typeof value.fechaFin === 'string' ? new Date(value.fechaFin) : value.fechaFin,
        fechaInicio: value.fechaInicio,
        persona,
        rolProyecto: value.rolProyecto,
        proyectoSocio: value.proyectoSocio
      };
      return result;
    }

    fromTarget(value: IProyectoSocioEquipo): IProyectoSocioEquipoBackend {
      const result: IProyectoSocioEquipoBackend = {
        id: value.id,
        fechaFin: value.fechaFin,
        fechaInicio: value.fechaInicio,
        personaRef: value.persona.personaRef,
        rolProyecto: value.rolProyecto,
        proyectoSocio: value.proyectoSocio
      };
      return result;
    }
  }();

  constructor(
    logger: NGXLogger,
    protected http: HttpClient
  ) {
    super(
      ProyectoSocioEquipoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoSocioEquipoService.MAPPING}`,
      http,
      ProyectoSocioEquipoService.CONVERTER
    );
  }

  /**
   * Actualiza el listado de IProyectoSocioEquipo asociados a un IProyectoEquipo
   *
   * @param id Id del IProyectoEquipo
   * @param entities Listado de IProyectoSocioEquipo
   */
  updateList(id: number, entities: IProyectoSocioEquipo[]): Observable<IProyectoSocioEquipo[]> {
    this.logger.debug(ProyectoSocioEquipoService.name, `updateList()`, '-', 'start');
    const entitiesBack = entities.map(entity => this.converter.fromTarget(entity));
    return this.http.patch<IProyectoSocioEquipoBackend[]>(`${this.endpointUrl}/${id}`, entitiesBack).pipe(
      map(resultList => resultList.map(entity => this.converter.toTarget(entity))),
      tap(() => this.logger.debug(ProyectoSocioEquipoService.name, `updateList()`, '-', 'end'))
    );
  }
}
