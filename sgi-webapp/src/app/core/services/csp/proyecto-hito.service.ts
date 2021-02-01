import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class ProyectoHitoService extends SgiRestService<number, IProyectoHito> {
  private static readonly MAPPING = '/proyectohitos';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoHitoService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoHitoService.MAPPING}`,
      http
    );
  }

}