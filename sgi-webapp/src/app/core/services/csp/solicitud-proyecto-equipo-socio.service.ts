import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SOLICITUD_PROYECTO_EQUIPO_SOCIO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-equipo-socio.converter';
import { ISolicitudProyectoEquipoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-equipo-socio-backend';
import { ISolicitudProyectoEquipoSocio } from '@core/models/csp/solicitud-proyecto-equipo-socio';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoEquipoSocioService
  extends SgiMutableRestService<number, ISolicitudProyectoEquipoSocioBackend, ISolicitudProyectoEquipoSocio> {
  private static readonly MAPPING = '/solicitudproyectoequiposocio';

  constructor(
    protected http: HttpClient
  ) {
    super(
      SolicitudProyectoEquipoSocioService.name,
      `${environment.serviceServers.csp}${SolicitudProyectoEquipoSocioService.MAPPING}`,
      http,
      SOLICITUD_PROYECTO_EQUIPO_SOCIO_CONVERTER
    );
  }

  updateList(proyectoSolictudSocioId: number, entities: ISolicitudProyectoEquipoSocio[]): Observable<ISolicitudProyectoEquipoSocio[]> {
    return this.http.patch<ISolicitudProyectoEquipoSocioBackend[]>(
      `${this.endpointUrl}/${proyectoSolictudSocioId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
