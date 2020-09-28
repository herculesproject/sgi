import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class DocumentacionMemoriaService extends SgiRestService<number, IDocumentacionMemoria>{
  private static readonly MAPPING = '/documentacionmemorias';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(DocumentacionMemoriaService.name, logger, `${environment.serviceServers.eti}${DocumentacionMemoriaService.MAPPING}`, http);
  }
}
