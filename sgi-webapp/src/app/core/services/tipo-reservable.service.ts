import {Injectable} from '@angular/core';
import {TipoReservable} from '@core/models/tipo-reservable';
import {NGXLogger} from 'ngx-logger';
import {HttpClient} from '@angular/common/http';
import {BaseService} from './base.service';

@Injectable({
  providedIn: 'root'
})
export class TipoReservableService extends BaseService<TipoReservable> {

  public static TIPORESERVABLE_MAPPING = '/tiporeservables';

  constructor(protected logger: NGXLogger, protected http: HttpClient) {
    super(logger, http, TipoReservableService.TIPORESERVABLE_MAPPING);
    this.logger.debug(
      TipoReservableService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'start'
    );
    this.logger.debug(
      TipoReservableService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'end'
    );
  }
}
