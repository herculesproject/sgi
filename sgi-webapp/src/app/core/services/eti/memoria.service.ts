import { Injectable } from '@angular/core';
import { BaseRestService } from '../base-rest.service';
import { Memoria } from '@core/models/eti/memoria';
import { UrlUtils } from '@core/utils/url-utils';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MemoriaService extends BaseRestService<Memoria>{

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(MemoriaService.name, logger, `${environment.apiUrl}/${UrlUtils.eti.memorias}`, http);
  }
}
