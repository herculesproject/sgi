import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class ModeloUnidadService extends SgiRestService<number, IModeloUnidad> {
  private static readonly MAPPING = '/modelounidades';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ModeloUnidadService.name,
      logger,
      `${environment.serviceServers.csp}${ModeloUnidadService.MAPPING}`,
      http
    );
  }
}
