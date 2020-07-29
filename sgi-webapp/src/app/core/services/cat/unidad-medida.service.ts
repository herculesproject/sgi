import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UnidadMedida } from '@core/models/cat/unidad-medida';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root',
})
export class UnidadMedidaService extends SgiRestService<UnidadMedida> {
  private static readonly MAPPING = '/unidadmedidas';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      UnidadMedidaService.name,
      logger,
      `${environment.serviceServers.cat}${UnidadMedidaService.MAPPING}`,
      http
    );
  }
}
