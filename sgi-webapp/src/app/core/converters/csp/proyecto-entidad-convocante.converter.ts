import { IProyectoEntidadConvocanteBackend } from '@core/models/csp/backend/proyecto-entidad-convocante-backend';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoEntidadConvocanteConverter extends SgiBaseConverter<IProyectoEntidadConvocanteBackend, IProyectoEntidadConvocante> {

  toTarget(value: IProyectoEntidadConvocanteBackend): IProyectoEntidadConvocante {
    if (!value) {
      return value as unknown as IProyectoEntidadConvocante;
    }
    return {
      id: value.id,
      entidad: { personaRef: value.entidadRef } as IEmpresaEconomica,
      programaConvocatoria: value.programaConvocatoria,
      programa: value.programa
    };
  }

  fromTarget(value: IProyectoEntidadConvocante): IProyectoEntidadConvocanteBackend {
    if (!value) {
      return value as unknown as IProyectoEntidadConvocanteBackend;
    }
    return {
      id: value.id,
      entidadRef: value.entidad?.personaRef,
      programaConvocatoria: value.programaConvocatoria,
      programa: value.programa
    };
  }
}

export const PROYECTO_ENTIDAD_CONVOCANTE_CONVERTER = new ProyectoEntidadConvocanteConverter();
