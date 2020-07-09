import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';


import { BaseRestService } from './base-rest.service';
import { Supervision } from '@core/models/supervision';
import { environment } from '@env';

@Injectable({
  providedIn: 'root'
})
export class SupervisionService extends BaseRestService<Supervision>{
  public static SUPERVISION_MAPPING = '/supervisiones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(SupervisionService.name, logger,
      `${environment.apiUrl}` + SupervisionService.SUPERVISION_MAPPING, http);
  }

}
