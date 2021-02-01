import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { environment } from '@env';
import { SgiBaseConverter } from '@sgi/framework/core';
import { SgiMutableRestService } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';

export interface IProyectoEntidadGestoraBackend {
  id: number;
  proyecto: IProyecto;
  entidadRef: string;
}
@Injectable({
  providedIn: 'root'
})
export class ProyectoEntidadGestoraService extends SgiMutableRestService<number, IProyectoEntidadGestoraBackend, IProyectoEntidadGestora> {
  private static readonly MAPPING = '/proyectoentidadgestoras';

  static readonly CONVERTER = new class extends SgiBaseConverter<IProyectoEntidadGestoraBackend, IProyectoEntidadGestora> {
    toTarget(value: IProyectoEntidadGestoraBackend): IProyectoEntidadGestora {
      return {
        id: value.id,
        proyecto: value.proyecto,
        empresaEconomica: { personaRef: value.entidadRef } as IEmpresaEconomica
      };
    }

    fromTarget(value: IProyectoEntidadGestora): IProyectoEntidadGestoraBackend {
      return {
        id: value.id,
        proyecto: value.proyecto,
        entidadRef: value.empresaEconomica?.personaRef
      };
    }
  }();

  constructor(protected readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoEntidadGestoraService.name,
      logger,
      `${environment.serviceServers.csp}${ProyectoEntidadGestoraService.MAPPING}`,
      http,
      ProyectoEntidadGestoraService.CONVERTER
    );
  }
}

