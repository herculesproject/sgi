import { Injectable } from '@angular/core';
import { SgiRestService } from '@sgi/framework/http';
import { Evaluacion } from '@core/models/eti/evaluacion';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class EvaluacionService extends SgiRestService<Evaluacion>{
  private static readonly MAPPING = '/evaluaciones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      EvaluacionService.name,
      logger,
      `${environment.serviceServers.eti}${EvaluacionService.MAPPING}`,
      http
    );
  }
}
