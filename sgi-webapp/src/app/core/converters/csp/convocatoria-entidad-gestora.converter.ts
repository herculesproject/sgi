import { IConvocatoriaEntidadGestoraBackend } from '@core/models/csp/backend/convocatoria-entidad-gestora-backend';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaEntidadGestoraConverter extends SgiBaseConverter<IConvocatoriaEntidadGestoraBackend, IConvocatoriaEntidadGestora> {

  toTarget(value: IConvocatoriaEntidadGestoraBackend): IConvocatoriaEntidadGestora {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadGestora;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      empresaEconomica: { personaRef: value.entidadRef } as IEmpresaEconomica
    };
  }

  fromTarget(value: IConvocatoriaEntidadGestora): IConvocatoriaEntidadGestoraBackend {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadGestoraBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      entidadRef: value.empresaEconomica?.personaRef
    };
  }
}

export const CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER = new ConvocatoriaEntidadGestoraConverter();
