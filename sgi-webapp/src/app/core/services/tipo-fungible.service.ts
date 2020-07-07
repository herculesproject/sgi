import { Injectable } from '@angular/core';
import { TipoFungible } from '@core/models/tipo-fungible';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseRestService } from './base-rest.service';
import { environment } from '@env';

@Injectable({
  providedIn: 'root'
})
export class TipoFungibleService extends BaseRestService<TipoFungible> {

  public static TIPOFUNGIBLE_MAPPING = '/tipofungibles';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(TipoFungibleService.name, logger, `${environment.apiUrl}` + TipoFungibleService.TIPOFUNGIBLE_MAPPING, http);
  }
}
