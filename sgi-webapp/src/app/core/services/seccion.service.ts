import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';

import { BaseService } from './base.service';

import { Seccion } from '@core/models/seccion';

@Injectable({
  providedIn: 'root',
})
export class SeccionService extends BaseService<Seccion> {
  public static SECCION_MAPPING = '/secciones';

  constructor(protected logger: NGXLogger, protected http: HttpClient) {
    super(logger, http, SeccionService.SECCION_MAPPING);
    this.logger.debug(
      SeccionService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'start'
    );
    this.logger.debug(
      SeccionService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'end'
    );
  }
}
