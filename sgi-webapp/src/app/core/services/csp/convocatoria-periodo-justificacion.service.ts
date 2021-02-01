import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { SgiRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaPeriodoJustificacionService extends SgiRestService<number, IConvocatoriaPeriodoJustificacion> {
  private static readonly MAPPING = '/convocatoriaperiodojustificaciones';

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ConvocatoriaPeriodoJustificacionService.name,
      logger,
      `${environment.serviceServers.csp}${ConvocatoriaPeriodoJustificacionService.MAPPING}`,
      http
    );
  }

  /**
   * Actualiza la lista IConvocatoriaPeriodoJustificacion de la convocatoria con los periodos de periodosJustificacion
   * creando los nuevos, actualizando los existentes y eliminando los existentes que no esten en la lista.
   *
   * @param convocatoriaId Id de la convocatoria
   * @param periodosJustificacion Lista de IConvocatoriaPeriodoJustificacion
   * @returns Lista de IConvocatoriaPeriodoJustificacion actualizada
   */
  updateConvocatoriaPeriodoJustificacionesConvocatoria(convocatoriaId: number, periodosJustificacion: IConvocatoriaPeriodoJustificacion[]):
    Observable<IConvocatoriaPeriodoJustificacion[]> {
    return this.http.patch<IConvocatoriaPeriodoJustificacion[]>(`${this.endpointUrl}/${convocatoriaId}`, periodosJustificacion);
  }

}
