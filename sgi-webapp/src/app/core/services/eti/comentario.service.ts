import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Comentario } from '@core/models/eti/comentario';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class ComentarioService extends SgiRestService<number, Comentario>{
  private static readonly MAPPING = '/comentarios';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ComentarioService.name,
      logger,
      `${environment.serviceServers.eti}${ComentarioService.MAPPING}`,
      http
    );
  }
}
