import { Injectable } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { BaseRestService } from '@core/services/base-rest.service';
import { environment } from '@env';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';

@Injectable({
  providedIn: 'root',
})
export class TipoConvocatoriaReunionService extends BaseRestService<TipoConvocatoriaReunion> {
  public static TIPO_CONVOCATORIA_REUNIONES_MAPPING = '/tipoconvocatoriareuniones';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(TipoConvocatoriaReunionService.name, logger,
      `${environment.apiUrl}` + TipoConvocatoriaReunionService.TIPO_CONVOCATORIA_REUNIONES_MAPPING, http);
  }
}
