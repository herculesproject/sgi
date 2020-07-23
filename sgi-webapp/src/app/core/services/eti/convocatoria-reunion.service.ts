import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { BaseRestService } from '@core/services/base-rest.service';

@Injectable({
  providedIn: 'root',
})
export class ConvocatoriaReunionService extends BaseRestService<ConvocatoriaReunion> {
  public static CONVOCATORIA_REUNION_MAPPING = '/convocatoriareuniones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(ConvocatoriaReunionService.name, logger,
      `${environment.apiUrl}` + ConvocatoriaReunionService.CONVOCATORIA_REUNION_MAPPING, http);
  }
}
