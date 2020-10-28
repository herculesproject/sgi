import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAreaTematicaArbol } from '@core/models/csp/area-tematica-arbol';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class AreaTematicaArbolService extends SgiRestService<number, IAreaTematicaArbol> {

  private static readonly MAPPING = '/areatematicaarboles';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      AreaTematicaArbolService.name,
      logger,
      `${environment.serviceServers.csp}${AreaTematicaArbolService.MAPPING}`,
      http
    );
  }
}
