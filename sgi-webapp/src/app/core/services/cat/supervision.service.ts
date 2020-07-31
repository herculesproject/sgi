import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Supervision } from '@core/models/cat/supervision';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class SupervisionService extends SgiRestService<number, Supervision>{
  private static readonly MAPPING = '/supervisiones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      SupervisionService.name,
      logger,
      `${environment.serviceServers.cat}${SupervisionService.MAPPING}`,
      http
    );
  }

}
