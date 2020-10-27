import { Injectable } from '@angular/core';
import { SgiRestService } from '@sgi/framework/http/';
import { IDocumento } from '@core/models/sgdoc/documento';
import { NGXLogger } from 'ngx-logger';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '@env';
import { Observable, of, EMPTY } from 'rxjs';
import { tap, switchMap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DocumentoService extends SgiRestService<string, IDocumento>{
  private static readonly MAPPING = '/documentos';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(DocumentoService.name, logger,
      `${environment.serviceServers.sgdoc}${DocumentoService.MAPPING}`, http);
  }

  /**
   * Crea el fichero en sgdoc.
   * @param fichero Fichero a crear.
   */
  uploadFichero(fichero: any): Observable<IDocumento> {
    this.logger.debug(DocumentoService.name, `uploadFichero()`, '-', 'START');

    const formData = new FormData();
    formData.append('archivo', fichero);

    return this.http.post<IDocumento>(`${this.endpointUrl}`, formData).pipe(
      switchMap((value) => {

        return of(value);
      }),
      catchError(() => {
        return EMPTY;
      })
    );

  }

  /**
   * Recupera la información del documento.
   * @param documentoRef referencia del documento.
   */
  getInfoFichero(documentoRef: string): Observable<IDocumento> {
    this.logger.debug(DocumentoService.name, `downloadFichero(${documentoRef}: string)`, '-', 'START');

    return this.http.get<IDocumento>(`${this.endpointUrl}/${documentoRef}`).pipe(
      tap(() => this.logger.debug(DocumentoService.name, `downloadFichero(${documentoRef}: string)`, '-', 'end'))
    );

  }


  /**
   * Descarga el fichero.
   * @param documentoRef referencia del documento.
   */
  downloadFichero(documentoRef: string): Observable<any> {
    this.logger.debug(DocumentoService.name, `downloadFichero(${documentoRef}: string)`, '-', 'START');

    return this.http.get(`${this.endpointUrl}/${documentoRef}/archivo`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    }).pipe(
      tap(() => this.logger.debug(DocumentoService.name, `downloadFichero(${documentoRef}: string)`, '-', 'end'))
    );

  }


  /**
   * Elimina el documento.
   * @param documentoRef referencia del documento.
   */
  eliminarFichero(documentoRef: string): Observable<void> {
    this.logger.debug(DocumentoService.name, `downloadFichero(${documentoRef}: string)`, '-', 'START');


    return this.http.delete<void>(`${this.endpointUrl}/${documentoRef}`).pipe(
      tap(() => this.logger.debug(DocumentoService.name, `downloadFichero(${documentoRef}: string)`, '-', 'end'))
    );

  }
}
