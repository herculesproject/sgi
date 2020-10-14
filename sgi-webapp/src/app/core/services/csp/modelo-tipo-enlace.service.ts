import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { ModeloEjecucionService } from './modelo-ejecucion.service';

@Injectable({
  providedIn: 'root'
})
export class ModeloTipoEnlaceService extends SgiRestService<number, IModeloTipoEnlace> {
  private static readonly MAPPING = '/modelotipoenlaces';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ModeloEjecucionService.name,
      logger,
      `${environment.serviceServers.csp}${ModeloTipoEnlaceService.MAPPING}`,
      http
    );
  }
}
