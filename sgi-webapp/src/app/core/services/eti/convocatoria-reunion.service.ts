import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { UrlUtils } from '@core/utils/url-utils';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';

import { BaseRestService } from '../base-rest.service';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaReunionService extends BaseRestService<ConvocatoriaReunion> {
  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(ConvocatoriaReunionService.name, logger, `${environment.apiUrl}/${UrlUtils.eti.convocatoriaReuniones}`, http);
  }
}
