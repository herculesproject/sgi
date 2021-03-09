import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IEmpresaEconomica } from '@core/models/sgp/empresa-economica';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaEntidadFinanciadoraConverter extends
  SgiBaseConverter<IConvocatoriaEntidadFinanciadoraBackend, IConvocatoriaEntidadFinanciadora> {

  toTarget(value: IConvocatoriaEntidadFinanciadoraBackend): IConvocatoriaEntidadFinanciadora {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadFinanciadora;
    }
    return {
      id: value.id,
      empresa: { personaRef: value.entidadRef } as IEmpresaEconomica,
      convocatoria: value?.convocatoria,
      fuenteFinanciacion: value.fuenteFinanciacion,
      tipoFinanciacion: value.tipoFinanciacion,
      porcentajeFinanciacion: value.porcentajeFinanciacion
    };
  }

  fromTarget(value: IConvocatoriaEntidadFinanciadora): IConvocatoriaEntidadFinanciadoraBackend {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadFinanciadoraBackend;
    }
    return {
      id: value.id,
      entidadRef: value.empresa?.personaRef,
      convocatoria: value?.convocatoria,
      fuenteFinanciacion: value.fuenteFinanciacion,
      tipoFinanciacion: value.tipoFinanciacion,
      porcentajeFinanciacion: value.porcentajeFinanciacion
    };
  }
}

export const CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER = new ConvocatoriaEntidadFinanciadoraConverter();
