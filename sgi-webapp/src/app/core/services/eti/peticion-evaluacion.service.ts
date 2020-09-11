import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class PeticionEvaluacionService extends SgiRestService<number, PeticionEvaluacion> {
  private static readonly MAPPING = '/peticionevaluaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      PeticionEvaluacionService.name,
      logger,
      `${environment.serviceServers.eti}${PeticionEvaluacionService.MAPPING}`,
      http
    );
  }
}
