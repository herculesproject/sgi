import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { SgiRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root',
})
export class ConvocatoriaReunionService extends SgiRestService<number, ConvocatoriaReunion> {
  private static readonly MAPPING = '/convocatoriareuniones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(
      ConvocatoriaReunionService.name,
      logger,
      `${environment.serviceServers.eti}${ConvocatoriaReunionService.MAPPING}`,
      http
    );
  }
}
