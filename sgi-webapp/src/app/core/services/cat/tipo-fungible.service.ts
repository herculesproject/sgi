import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoFungible } from '@core/models/cat/tipo-fungible';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class TipoFungibleService extends SgiRestService<number, TipoFungible> {
  private static readonly MAPPING = '/tipofungibles';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      TipoFungibleService.name,
      logger,
      `${environment.serviceServers.cat}${TipoFungibleService.MAPPING}`,
      http
    );
  }
}
