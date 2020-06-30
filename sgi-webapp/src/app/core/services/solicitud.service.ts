import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { Registro } from '@core/models/registro';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class SolicitudService extends BaseService<Registro> {

  public static SOLICITUD_MAPPING = '/registros';
  public registro: Registro;

  constructor(protected logger: NGXLogger, protected http: HttpClient) {
    super(logger, http, SolicitudService.SOLICITUD_MAPPING);
    this.logger.debug(
      SolicitudService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'start'
    );
    this.logger.debug(
      SolicitudService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'end'
    );
  }
}
