import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ContextoProyectoService extends SgiRestService<number, IProyectoContexto> {
  private static readonly MAPPING = '/proyecto-contextoproyectos';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ContextoProyectoService.name,
      logger,
      `${environment.serviceServers.csp}${ContextoProyectoService.MAPPING}`,
      http
    );
  }

}
