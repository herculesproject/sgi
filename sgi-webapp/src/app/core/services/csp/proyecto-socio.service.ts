import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { environment } from '@env';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SgiMutableRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface IProyectoSocioBackend {
  id: number;
  proyecto: IProyecto;
  empresaRef: string;
  rolSocio: IRolSocio;
  fechaInicio: Date;
  fechaFin: Date;
  numInvestigadores: number;
  importeConcedido: number;
}


@Injectable({
  providedIn: 'root'
})
export class ProyectoSocioService extends SgiMutableRestService<number, IProyectoSocioBackend, IProyectoSocio>  {
  private static readonly MAPPING = '/proyectosocios';

  static CONVERTER = new class extends SgiBaseConverter<IProyectoSocioBackend, IProyectoSocio> {
    toTarget(value: IProyectoSocioBackend): IProyectoSocio {
      return {
        id: value.id,
        proyecto: value.proyecto,
        empresa: { personaRef: value.empresaRef } as IEmpresaEconomica,
        rolSocio: value.rolSocio,
        fechaInicio: value.fechaInicio,
        fechaFin: value.fechaFin,
        numInvestigadores: value.numInvestigadores,
        importeConcedido: value.importeConcedido
      };
    }

    fromTarget(value: IProyectoSocio): IProyectoSocioBackend {
      return {
        id: value.id,
        proyecto: value.proyecto,
        empresaRef: value.empresa.personaRef,
        rolSocio: value.rolSocio,
        fechaInicio: value.fechaInicio,
        fechaFin: value.fechaFin,
        numInvestigadores: value.numInvestigadores,
        importeConcedido: value.importeConcedido
      };
    }
  }();


  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoSocioService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoSocioService.MAPPING}`,
      http,
      ProyectoSocioService.CONVERTER
    );
  }

  /**
   * Comprueba si existe un proyecto socio
   *
   * @param id Id del proyecto socio
   */
  exists(id: number): Observable<boolean> {
    this.logger.debug(ProyectoSocioService.name, `exists(id: ${id})`, '-', 'start');
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200),
      tap(() => this.logger.debug(ProyectoSocioService.name, `exists(id: ${id})`, '-', 'end')),
    );
  }

}
