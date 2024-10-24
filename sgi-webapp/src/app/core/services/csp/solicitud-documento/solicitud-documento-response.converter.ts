import { ISolicitudDocumentoResponse } from '@core/services/csp/solicitud-documento/solicitud-documento-response';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_FASE_RESPONSE_CONVERTER } from '../tipo-fase/tipo-fase-response.converter';

class SolicitudDocumentoResponseConverter extends SgiBaseConverter<ISolicitudDocumentoResponse, ISolicitudDocumento> {

  toTarget(value: ISolicitudDocumentoResponse): ISolicitudDocumento {
    if (!value) {
      return value as unknown as ISolicitudDocumento;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      comentario: value.comentario,
      documentoRef: value.documentoRef,
      nombre: value.nombre,
      tipoDocumento: value.tipoDocumento,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null
    };
  }

  fromTarget(value: ISolicitudDocumento): ISolicitudDocumentoResponse {
    if (!value) {
      return value as unknown as ISolicitudDocumentoResponse;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      comentario: value.comentario,
      documentoRef: value.documentoRef,
      nombre: value.nombre,
      tipoDocumento: value.tipoDocumento,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null
    };
  }
}

export const SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER = new SolicitudDocumentoResponseConverter();
