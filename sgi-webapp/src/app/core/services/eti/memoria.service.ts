import { Injectable } from '@angular/core';
import { SgiRestService } from '@sgi/framework/http';
import { Memoria } from '@core/models/eti/memoria';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MemoriaService extends SgiRestService<Memoria>{
  private static readonly MAPPING = '/memorias';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      MemoriaService.name,
      logger,
      `${environment.serviceServers.eti}${MemoriaService.MAPPING}`,
      http
    );
  }
}
