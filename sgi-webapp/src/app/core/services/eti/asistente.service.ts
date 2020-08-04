import { Injectable } from '@angular/core';
import { Asistente } from '@core/models/eti/asistente';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class AsistenteService extends SgiRestService<number, Asistente>{

  private static readonly MAPPING = '/asistentes';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(AsistenteService.name, logger, `${environment.serviceServers.eti}` + AsistenteService.MAPPING, http);
  }
}
