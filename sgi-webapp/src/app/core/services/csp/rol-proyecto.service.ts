import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class RolProyectoService extends SgiRestService<number, IRolProyecto> {
  private static readonly MAPPING = '/rolproyectos';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      RolProyectoService.name,
      logger,
      `${environment.serviceServers.csp}${RolProyectoService.MAPPING}`,
      http
    );
  }
}
