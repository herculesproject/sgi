import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService, SgiRestListResult, SgiRestFindOptions } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { IEquipoTrabajo } from '@core/models/eti/equipo-trabajo';
import { IMemoriaPeticionEvaluacion } from '@core/models/eti/memoriaPeticionEvaluacion';

@Injectable({
  providedIn: 'root'
})
export class PeticionEvaluacionService extends SgiRestService<number, IPeticionEvaluacion> {
  private static readonly MAPPING = '/peticionevaluaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      PeticionEvaluacionService.name,
      logger,
      `${environment.serviceServers.eti}${PeticionEvaluacionService.MAPPING}`,
      http
    );
  }

  /**
   * Devuelve los equipos de trabajo de la PeticionEvaluacion.
   *
   * @param idPeticionEvaluacion id de la peticion de evaluacion.
   * @return los equipos de trabajo de la PeticionEvaluacion.
   */
  findEquipoInvestigador(idPeticionEvaluacion: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEquipoTrabajo>> {
    this.logger.debug(PeticionEvaluacionService.name, `findEquipoInvestigador(${idPeticionEvaluacion})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idPeticionEvaluacion}/equipo-investigador`;
    return this.find<IEquipoTrabajo, IEquipoTrabajo>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(PeticionEvaluacionService.name, `findEquipoInvestigador(${idPeticionEvaluacion})`, '-', 'end'))
      );
  }

  /**
   * Devuelve las memorias de la PeticionEvaluacion.
   *
   * @param idPeticionEvaluacion id de la peticion de evaluacion.
   * @return las memorias de la PeticionEvaluacion.
   */
  findMemorias(idPeticionEvaluacion: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoriaPeticionEvaluacion>> {
    this.logger.debug(PeticionEvaluacionService.name, `findMemorias(${idPeticionEvaluacion})`, '-', 'start');
    const endpointUrl = `${this.endpointUrl}/${idPeticionEvaluacion}/memorias`;
    return this.find<IMemoriaPeticionEvaluacion, IMemoriaPeticionEvaluacion>(endpointUrl, options)
      .pipe(
        tap(() => this.logger.debug(PeticionEvaluacionService.name, `findMemorias(${idPeticionEvaluacion})`, '-', 'end'))
      );
  }


}
