import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { UnidadMedida } from '@core/models/unidad-medida';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class UnidadMedidaService extends BaseService<UnidadMedida> {

  public static UNIDADMEDIDA_MAPPING = '/unidadmedidas/all';

  constructor(protected logger: NGXLogger, protected http: HttpClient) {
    super(logger, http, UnidadMedidaService.UNIDADMEDIDA_MAPPING);
    this.logger.debug(UnidadMedidaService.name, 'constructor(protected logger: NGXLogger, protected http: HttpClient)', 'start');
    this.logger.debug(UnidadMedidaService.name, 'constructor(protected logger: NGXLogger, protected http: HttpClient)', 'end');
  }


}
