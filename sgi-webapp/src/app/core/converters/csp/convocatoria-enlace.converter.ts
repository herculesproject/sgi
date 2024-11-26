import { IConvocatoriaEnlaceBackend } from '@core/models/csp/backend/convocatoria-enlace-backend';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { TIPO_ENLACE_RESPONSE_CONVERTER } from '@core/services/csp/tipo-enlace/tipo-enlace-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaEnlaceConverter extends SgiBaseConverter<IConvocatoriaEnlaceBackend, IConvocatoriaEnlace> {

  toTarget(value: IConvocatoriaEnlaceBackend): IConvocatoriaEnlace {
    if (!value) {
      return value as unknown as IConvocatoriaEnlace;
    }
    return {
      id: value.id,
      tipoEnlace: TIPO_ENLACE_RESPONSE_CONVERTER.toTarget(value.tipoEnlace),
      activo: value.activo,
      convocatoriaId: value.convocatoriaId,
      url: value.url,
      descripcion: value.descripcion
    };
  }

  fromTarget(value: IConvocatoriaEnlace): IConvocatoriaEnlaceBackend {
    if (!value) {
      return value as unknown as IConvocatoriaEnlaceBackend;
    }
    return {
      id: value.id,
      tipoEnlace: TIPO_ENLACE_RESPONSE_CONVERTER.fromTarget(value.tipoEnlace),
      activo: value.activo,
      convocatoriaId: value.convocatoriaId,
      url: value.url,
      descripcion: value.descripcion
    };
  }
}

export const CONVOCATORIA_ENLACE_CONVERTER = new ConvocatoriaEnlaceConverter();
