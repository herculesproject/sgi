import { Injectable } from '@angular/core';
import { TipoReservable } from '@core/models/tipo-reservable';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseRestService } from './base-rest.service';
import { environment } from '@env';

@Injectable({
  providedIn: 'root'
})
export class TipoReservableService extends BaseRestService<TipoReservable> {

  public static TIPORESERVABLE_MAPPING = '/tiporeservables';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoReservableService.name,
      logger,
      `${environment.apiUrl}` + TipoReservableService.TIPORESERVABLE_MAPPING,
      http);
  }
}
