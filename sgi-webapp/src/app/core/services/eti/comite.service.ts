import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseRestService } from '@core/services/base-rest.service';
import { environment } from '@env';
import { Comite } from '@core/models/eti/comite';

@Injectable({
  providedIn: 'root',
})
export class ComiteService extends BaseRestService<Comite> {
  public static COMITE_MAPPING = '/comites';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(ComiteService.name, logger,
      `${environment.apiUrl}` + ComiteService.COMITE_MAPPING, http);
  }
}
