import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoReservable } from '@core/models/cat/tipo-reservable';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoReservableService extends SgiRestService<TipoReservable> {
  private static readonly MAPPING = '/tiporeservables';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoReservableService.name,
      logger,
      `${environment.serviceServers.cat}${TipoReservableService.MAPPING}`,
      http
    );
  }
}
