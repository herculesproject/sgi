import { Injectable } from '@angular/core';
import { Usuario } from '@core/models/sgp/usuario';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { SgiRestService } from '@sgi/framework/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';



@Injectable({
  providedIn: 'root'
})
export class UsuarioService extends SgiRestService<string, Usuario>{
  private static readonly MAPPING = '/usuarios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      UsuarioService.name,
      logger,
      `${environment.serviceServers.sgp}${UsuarioService.MAPPING}`,
      http
    );
  }

}
