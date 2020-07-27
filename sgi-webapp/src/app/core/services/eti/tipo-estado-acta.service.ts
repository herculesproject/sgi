import { Injectable } from '@angular/core';
import { TipoEstadoActa } from '@core/models/eti/tipo-estado-acta';
import { BaseRestService } from '../base-rest.service';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { UrlUtils } from '@core/utils/url-utils';

@Injectable({
  providedIn: 'root'
})
export class TipoEstadoActaService extends BaseRestService<TipoEstadoActa> {

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(TipoEstadoActaService.name, logger, `${environment.apiUrl}/${UrlUtils.eti.tipoEstadoActa}`, http);
  }
}
