import {Injectable} from '@angular/core';
import {Servicio} from '@core/models/servicio';
import {NGXLogger} from 'ngx-logger';
import {HttpClient} from '@angular/common/http';
import {BaseService} from './base.service';

@Injectable({
  providedIn: 'root',
})
export class ServicioService extends BaseService<Servicio> {
  public static SERVICIO_MAPPING = '/servicios';

  constructor(protected logger: NGXLogger, protected http: HttpClient) {
    super(logger, http, ServicioService.SERVICIO_MAPPING);
    this.logger.debug(
      ServicioService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'start'
    );
    this.logger.debug(
      ServicioService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'end'
    );
  }
}
