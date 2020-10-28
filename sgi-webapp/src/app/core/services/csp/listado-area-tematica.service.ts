import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IListadoAreaTematica } from '@core/models/csp/listado-area-tematica';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class ListadoAreaTematicaService extends SgiRestService<number, IListadoAreaTematica> {

  private static readonly MAPPING = '/listadoareatematicas';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ListadoAreaTematicaService.name,
      logger,
      `${environment.serviceServers.csp}${ListadoAreaTematicaService.MAPPING}`,
      http
    );
  }
}
