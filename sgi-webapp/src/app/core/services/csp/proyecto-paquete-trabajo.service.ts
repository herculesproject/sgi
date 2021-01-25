import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPaqueteTrabajoService extends SgiRestService<number, IProyectoPaqueteTrabajo> {
  private static readonly MAPPING = '/proyectopaquetetrabajos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoPaqueteTrabajoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoPaqueteTrabajoService.MAPPING}`,
      http
    );
  }

}
