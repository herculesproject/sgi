import { IDocumentoRequeridoBackend } from '@core/models/csp/backend/documento-requerido-backend';
import { IDocumentoRequerido } from '@core/models/csp/documentos-requeridos-solicitud';
import { SgiBaseConverter } from '@sgi/framework/core';
import { CONFIGURACION_SOLICITUD_CONVERTER } from './configuracion-solicitud.converter';

class DocumentoRequeridoConverter extends
  SgiBaseConverter<IDocumentoRequeridoBackend, IDocumentoRequerido> {

  toTarget(value: IDocumentoRequeridoBackend): IDocumentoRequerido {
    if (!value) {
      return value as unknown as IDocumentoRequerido;
    }
    return {
      id: value.id,
      configuracionSolicitud: CONFIGURACION_SOLICITUD_CONVERTER.toTarget(value.configuracionSolicitud),
      tipoDocumento: value.tipoDocumento,
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IDocumentoRequerido): IDocumentoRequeridoBackend {
    if (!value) {
      return value as unknown as IDocumentoRequeridoBackend;
    }
    return {
      id: value.id,
      configuracionSolicitud: CONFIGURACION_SOLICITUD_CONVERTER.fromTarget(value.configuracionSolicitud),
      tipoDocumento: value.tipoDocumento,
      observaciones: value.observaciones
    };
  }
}

export const DOCUMENTO_REQUERIDO_CONVERTER = new DocumentoRequeridoConverter();
