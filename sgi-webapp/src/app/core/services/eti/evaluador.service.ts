import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEvaluador } from '@core/models/eti/evaluador';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class EvaluadorService extends SgiRestService<number, IEvaluador> {
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
