import { IDocumentoRequeridoSolicitudResponse } from '@core/services/csp/documento-requerido-solicitud/documento-requerido-solicitud-response';

import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';

class DocumentoRequeridoSolicitudConverter extends
  SgiBaseConverter<IDocumentoRequeridoSolicitudResponse, IDocumentoRequeridoSolicitud> {

  toTarget(value: IDocumentoRequeridoSolicitudResponse): IDocumentoRequeridoSolicitud {
    if (!value) {
      return value as unknown as IDocumentoRequeridoSolicitud;
    }
    return {
      id: value.id,
      configuracionSolicitudId: value.configuracionSolicitudId,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IDocumentoRequeridoSolicitud): IDocumentoRequeridoSolicitudResponse {
    if (!value) {
      return value as unknown as IDocumentoRequeridoSolicitudResponse;
    }
    return {
      id: value.id,
      configuracionSolicitudId: value.configuracionSolicitudId,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      observaciones: value.observaciones
    };
  }
}

export const DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER = new DocumentoRequeridoSolicitudConverter();
