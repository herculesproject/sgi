import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoDocumentoResponse } from './tipo-documento-response';

class TipoDocumentoResponseConverter extends SgiBaseConverter<ITipoDocumentoResponse, ITipoDocumento> {
  toTarget(value: ITipoDocumentoResponse): ITipoDocumento {
    if (!value) {
      return value as unknown as ITipoDocumento;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      nombre: value.nombre,
      formularioId: value.formularioId,
      adicional: value.adicional,
      activo: value.activo
    };
  }

  fromTarget(value: ITipoDocumento): ITipoDocumentoResponse {
    if (!value) {
      return value as unknown as ITipoDocumentoResponse;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      nombre: value.nombre,
      formularioId: value.formularioId,
      adicional: value.adicional,
      activo: value.activo
    };
  }
}

export const TIPO_DOCUMENTO_RESPONSE_CONVERTER = new TipoDocumentoResponseConverter();
