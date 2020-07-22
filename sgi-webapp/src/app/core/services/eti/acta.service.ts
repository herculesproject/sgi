import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Acta } from '@core/models/eti/acta';
import { UrlUtils } from '@core/utils/url-utils';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';

import { BaseRestService } from '../base-rest.service';

@Injectable({
  providedIn: 'root'
})
export class ActaService extends BaseRestService<Acta> {
  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(ActaService.name, logger, `${environment.apiUrl}/${UrlUtils.eti.actas}`, http);
  }
}
