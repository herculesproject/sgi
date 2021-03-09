import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_CONVERTER } from '@core/converters/csp/convocatoria-seguimiento-cientifico.converter';
import { IConvocatoriaSeguimientoCientificoBackend } from '@core/models/csp/backend/convocatoria-seguimiento-cientifico-backend';
import { IConvocatoriaSeguimientoCientifico } from '@core/models/csp/convocatoria-seguimiento-cientifico';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaSeguimientoCientificoService
  extends SgiMutableRestService<number, IConvocatoriaSeguimientoCientificoBackend, IConvocatoriaSeguimientoCientifico> {
  private static readonly MAPPING = '/convocatoriaperiodoseguimientocientificos';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaSeguimientoCientificoService.name,
      `${environment.serviceServers.csp}${ConvocatoriaSeguimientoCientificoService.MAPPING}`,
      http,
      CONVOCATORIA_SEGUIMIENTO_CIENTIFICO_CONVERTER
    );
  }

  /**
   * Actualiza la lista IConvocatoriaSeguimientoCientifico de la convocatoria con los periodos de seguimiento cientifico
   * creando los nuevos, actualizando los existentes y eliminando los existentes que no esten en la lista.
   *
   * @param convocatoriaId Id de la convocatoria
   * @param periodosJustificacion Lista de IConvocatoriaSeguimientoCientifico
   * @returns Lista de IConvocatoriaSeguimientoCientifico actualizada
   */
  updateConvocatoriaSeguimientoCientificoConvocatoria(convocatoriaId: number, periodosJustificacion: IConvocatoriaSeguimientoCientifico[]):
    Observable<IConvocatoriaSeguimientoCientifico[]> {
    return this.http.patch<IConvocatoriaSeguimientoCientificoBackend[]>(
      `${this.endpointUrl}/${convocatoriaId}`,
      this.converter.fromTargetArray(periodosJustificacion)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }

}
