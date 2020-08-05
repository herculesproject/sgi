import { Injectable } from '@angular/core';
import { Evaluador } from '@core/models/eti/evaluador';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { SgiRestService } from '@sgi/framework/http';


@Injectable({
  providedIn: 'root'
})
export class EvaluadorService extends SgiRestService<number, Evaluador>{

  private static readonly MAPPING = '/evaluadores';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      EvaluadorService.name,
      logger,
      `${environment.serviceServers.eti}${EvaluadorService.MAPPING}`,
      http
    );
  }

}
