import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { TipoFungible } from '@core/models/tipo-fungible';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class TipoFungibleService extends BaseService<TipoFungible> {

  public static TIPOFUNGIBLE_MAPPING = '/tipofungibles';

  constructor(protected logger: NGXLogger, protected http: HttpClient) {
    super(logger, http, TipoFungibleService.TIPOFUNGIBLE_MAPPING);
    this.logger.debug(
      TipoFungibleService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'start'
    );
    this.logger.debug(
      TipoFungibleService.name,
      'constructor(protected logger: NGXLogger, protected http: HttpClient)',
      'end'
    );
  }
}
