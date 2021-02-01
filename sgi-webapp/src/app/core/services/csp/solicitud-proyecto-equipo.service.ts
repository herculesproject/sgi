import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { ISolicitudProyectoDatos } from '@core/models/csp/solicitud-proyecto-datos';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPersona } from '@core/models/sgp/persona';

export interface ISolicitudProyectoEquipoBackend {
  id: number;
  solicitudProyectoDatos: ISolicitudProyectoDatos;
  personaRef: string;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoEquipoService extends
  SgiMutableRestService<number, ISolicitudProyectoEquipoBackend, ISolicitudProyectoEquipo> {
  private static readonly MAPPING = '/solicitudproyectoequipo';

  public static readonly CONVERTER = new class extends SgiBaseConverter<ISolicitudProyectoEquipoBackend, ISolicitudProyectoEquipo> {
    toTarget(value: ISolicitudProyectoEquipoBackend): ISolicitudProyectoEquipo {
      const result: ISolicitudProyectoEquipo = {
        id: value.id,
        mesFin: value.mesFin,
        mesInicio: value.mesInicio,
        persona: { personaRef: value.personaRef } as IPersona,
        rolProyecto: value.rolProyecto,
        solicitudProyectoDatos: value.solicitudProyectoDatos
      };
      return result;
    }

    fromTarget(value: ISolicitudProyectoEquipo): ISolicitudProyectoEquipoBackend {
      const result: ISolicitudProyectoEquipoBackend = {
        id: value.id,
        mesFin: value.mesFin,
        mesInicio: value.mesInicio,
        personaRef: value.persona.personaRef,
        rolProyecto: value.rolProyecto,
        solicitudProyectoDatos: value.solicitudProyectoDatos
      };
      return result;
    }
  }();

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudProyectoEquipoService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudProyectoEquipoService.MAPPING}`,
      http,
      SolicitudProyectoEquipoService.CONVERTER
    );
  }
}
