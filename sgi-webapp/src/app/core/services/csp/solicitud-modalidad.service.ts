import { Injectable } from '@angular/core';
import { SgiMutableRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env';
import { ISolicitud } from '@core/models/csp/solicitud';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IPrograma } from '@core/models/csp/programa';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';


export interface ISolicitudModalidadBackend {
  /** Id */
  id: number;

  /** Solicitud */
  solicitud: ISolicitud;

  /** EntidadRef */
  entidadRef: string;

  /** Programa */
  programa: IPrograma;

}


@Injectable({
  providedIn: 'root'
})
export class SolicitudModalidadService extends SgiMutableRestService<number, ISolicitudModalidadBackend, ISolicitudModalidad> {
  static readonly MAPPING = '/solicitudmodalidades';

  static readonly CONVERTER = new class extends SgiBaseConverter<ISolicitudModalidadBackend, ISolicitudModalidad> {
    toTarget(value: ISolicitudModalidadBackend): ISolicitudModalidad {
      return {
        id: value.id,
        solicitud: value.solicitud,
        entidad: { personaRef: value.entidadRef } as IEmpresaEconomica,
        programa: value.programa
      };
    }

    fromTarget(value: ISolicitudModalidad): ISolicitudModalidadBackend {
      return {
        id: value.id,
        solicitud: value.solicitud,
        entidadRef: value.entidad?.personaRef,
        programa: value.programa
      };
    }
  }();

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      SolicitudModalidadService.name,
      logger,
      `${environment.serviceServers.csp}${SolicitudModalidadService.MAPPING}`,
      http,
      SolicitudModalidadService.CONVERTER
    );
  }

}
