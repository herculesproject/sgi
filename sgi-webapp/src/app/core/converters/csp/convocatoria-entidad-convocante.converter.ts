import { IConvocatoriaEntidadConvocanteBackend } from '@core/models/csp/backend/convocatoria-entidad-convocante-backend';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaEntidadConvocanteConverter extends
  SgiBaseConverter<IConvocatoriaEntidadConvocanteBackend, IConvocatoriaEntidadConvocante> {

  toTarget(value: IConvocatoriaEntidadConvocanteBackend): IConvocatoriaEntidadConvocante {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadConvocante;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      entidad: { personaRef: value.entidadRef } as IEmpresaEconomica,
      programa: value.programa
    };
  }

  fromTarget(value: IConvocatoriaEntidadConvocante): IConvocatoriaEntidadConvocanteBackend {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadConvocanteBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      entidadRef: value.entidad?.personaRef,
      programa: value.programa
    };
  }
}

export const CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER = new ConvocatoriaEntidadConvocanteConverter();
