import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_CONVERTER
} from '@core/converters/csp/solicitud-proyecto-periodo-justificacion.converter';
import { ISolicitudProyectoPeriodoJustificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-periodo-justificacion-backend';
import { ISolicitudProyectoPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-periodo-justificacion';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoPeriodoJustificacionService
  extends SgiMutableRestService<number, ISolicitudProyectoPeriodoJustificacionBackend, ISolicitudProyectoPeriodoJustificacion> {
  private static readonly MAPPING = '/solicitudproyectoperiodojustificaciones';

  constructor(protected http: HttpClient) {
    super(
      SolicitudProyectoPeriodoJustificacionService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoPeriodoJustificacionService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoPeriodoJustificacion[]):
    Observable<ISolicitudProyectoPeriodoJustificacion[]> {
    return this.http.patch<ISolicitudProyectoPeriodoJustificacionBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
