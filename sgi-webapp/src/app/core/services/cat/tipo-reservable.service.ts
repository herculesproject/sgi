import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoReservable } from '@core/models/cat/tipo-reservable';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';

import { BaseRestService } from '../base-rest.service';

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
