import { Injectable } from '@angular/core';
import { BaseRestService } from '../base-rest.service';
import { Evaluacion } from '@core/models/eti/evaluacion';
import { UrlUtils } from '@core/utils/url-utils';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class EvaluacionService extends BaseRestService<Evaluacion>{

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(EvaluacionService.name, logger, `${environment.apiUrl}/${UrlUtils.eti.evaluacion}`, http);
  }
}
