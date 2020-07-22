import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TipoFungible } from '@core/models/cat/tipo-fungible';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';

import { BaseRestService } from '../base-rest.service';

@Injectable({
  providedIn: 'root'
})
export class TipoFungibleService extends BaseRestService<TipoFungible> {

  public static TIPOFUNGIBLE_MAPPING = '/tipofungibles';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(TipoFungibleService.name, logger, `${environment.apiUrl}` + TipoFungibleService.TIPOFUNGIBLE_MAPPING, http);
  }
}
