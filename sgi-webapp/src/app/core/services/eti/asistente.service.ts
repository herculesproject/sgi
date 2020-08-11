import { Injectable } from '@angular/core';
import { IAsistente } from '@core/models/eti/asistente';
import { environment } from '@env';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { SgiRestService, SgiRestFindOptions } from '@sgi/framework/http';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AsistenteService extends SgiRestService<number, IAsistente>{

  private static readonly MAPPING = '/asistentes';

  constructor(logger: NGXLogger, protected http: HttpClient) {
    super(AsistenteService.name, logger, `${environment.serviceServers.eti}` + AsistenteService.MAPPING, http);
  }

  /**
   * Devuelve todos los asitentes por convocatoria id.
   * @param idConvocatoria id convocatoria.
   */
  findAllByConvocatoriaReunionId(idConvocatoria: number) {
    this.logger.debug(AsistenteService.name, `findAllByConvocatoriaReunionId(${idConvocatoria})`, '-', 'START');
    return this.find<IAsistente, IAsistente>(`${this.endpointUrl}/convocatoriareunion/${idConvocatoria}`, null).pipe(
      tap(() => this.logger.debug(AsistenteService.name, `findAllByConvocatoriaReunionId(${idConvocatoria})`, '-', 'END'))
    );
  }

}
