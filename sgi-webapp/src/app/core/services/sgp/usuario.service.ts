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
export class UsuarioService extends SgiRestService<number, Usuario>{
  private static readonly MAPPING = '/usuarios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      UsuarioService.name,
      logger,
      `${environment.serviceServers.sgp}${UsuarioService.MAPPING}`,
      http
    );
  }

  /**
   * Find an element by their ID
   *
   * @param id The ID of the element
   */
  // TODO: Manage 404 (NotFound) and return an empty element?
  public findByRef(id: string): Observable<Usuario> {

    return this.http.get<Usuario>(`${this.endpointUrl}/${id}`).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a
      // custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        // Pass the error to subscribers. Anyway they would decide what to do
        // with the error.
        return throwError(error);
      }),
      map((response) => {
        return response;
      })
    );
  }

}
