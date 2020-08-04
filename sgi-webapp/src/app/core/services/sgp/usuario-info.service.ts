import { Injectable } from '@angular/core';
import { SgiRestService } from '@sgi/framework/http';
import { UsuarioInfo } from '@core/models/sgp/usuario-info';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { NGXLogger } from 'ngx-logger';
import { environment } from '@env';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UsuarioInfoService extends SgiRestService<string, UsuarioInfo>{
  private static readonly MAPPING = '/usuariosinfo';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      UsuarioInfoService.name,
      logger,
      `${environment.serviceServers.sgp}${UsuarioInfoService.MAPPING}`,
      http
    );
  }

}
