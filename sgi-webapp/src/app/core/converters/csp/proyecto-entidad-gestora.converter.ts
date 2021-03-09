import { IProyectoEntidadGestoraBackend } from '@core/models/csp/backend/proyecto-entidad-gestora-backend';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_CONVERTER } from './proyecto.converter';

class ProyectoEntidadGestoraConverter extends SgiBaseConverter<IProyectoEntidadGestoraBackend, IProyectoEntidadGestora> {

  toTarget(value: IProyectoEntidadGestoraBackend): IProyectoEntidadGestora {
    if (!value) {
      return value as unknown as IProyectoEntidadGestora;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.toTarget(value.proyecto),
      empresaEconomica: { personaRef: value.entidadRef } as IEmpresaEconomica
    };
  }

  fromTarget(value: IProyectoEntidadGestora): IProyectoEntidadGestoraBackend {
    if (!value) {
      return value as unknown as IProyectoEntidadGestoraBackend;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.fromTarget(value.proyecto),
      entidadRef: value.empresaEconomica?.personaRef
    };
  }
}

export const PROYECTO_ENTIDAD_GESTORA_CONVERTER = new ProyectoEntidadGestoraConverter();
