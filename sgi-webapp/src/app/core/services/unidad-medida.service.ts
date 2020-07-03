import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UnidadMedida } from '@core/models/unidad-medida';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';

import { BaseRestService } from './base-rest.service';

@Injectable({
  providedIn: 'root',
})
export class UnidadMedidaService extends BaseRestService<UnidadMedida> {
  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      UnidadMedidaService.name,
      logger,
      `${environment.apiUrl}/unidadmedidas`,
      http
    );
  }
}
