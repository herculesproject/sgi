import { IConvocatoriaDocumentoResponse } from '@core/services/csp/convocatoria-documento/convocatoria-documento-response';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_FASE_RESPONSE_CONVERTER } from '../tipo-fase/tipo-fase-response.converter';

class ConvocatoriaDocumentoConverter extends SgiBaseConverter<IConvocatoriaDocumentoResponse, IConvocatoriaDocumento> {

  toTarget(value: IConvocatoriaDocumentoResponse): IConvocatoriaDocumento {
    if (!value) {
      return value as unknown as IConvocatoriaDocumento;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento,
      publico: value.publico,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IConvocatoriaDocumento): IConvocatoriaDocumentoResponse {
    if (!value) {
      return value as unknown as IConvocatoriaDocumentoResponse;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento,
      publico: value.publico,
      observaciones: value.observaciones
    };
  }
}

export const CONVOCATORIA_DOCUMENTO_CONVERTER = new ConvocatoriaDocumentoConverter();
