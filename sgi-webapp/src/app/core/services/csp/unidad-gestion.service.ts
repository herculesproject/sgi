import { Injectable } from '@angular/core';
import { SgiRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class UnidadGestionService extends SgiRestService<number, IUnidadGestion> {
  private static readonly MAPPING = '/unidades';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      UnidadGestionService.name,
      logger,
      `${environment.serviceServers.usr}${UnidadGestionService.MAPPING}`,
      http
    );
  }
}
