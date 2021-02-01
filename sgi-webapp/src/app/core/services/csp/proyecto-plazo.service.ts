import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPlazos } from '@core/models/csp/proyecto-plazo';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPlazoService extends SgiRestService<number, IProyectoPlazos> {
  private static readonly MAPPING = '/proyectofases';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoPlazoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoPlazoService.MAPPING}`,
      http
    );
  }

}
