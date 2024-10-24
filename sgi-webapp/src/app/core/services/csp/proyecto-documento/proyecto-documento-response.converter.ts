import { IProyectoDocumentoResponse } from '@core/services/csp/proyecto-documento/proyecto-documento-response';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_FASE_RESPONSE_CONVERTER } from '../tipo-fase/tipo-fase-response.converter';

class ProyectoDocumentoResponseConverter extends SgiBaseConverter<IProyectoDocumentoResponse, IProyectoDocumento> {

  toTarget(value: IProyectoDocumentoResponse): IProyectoDocumento {
    if (!value) {
      return value as unknown as IProyectoDocumento;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }

  fromTarget(value: IProyectoDocumento): IProyectoDocumentoResponse {
    if (!value) {
      return value as unknown as IProyectoDocumentoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }
}

export const PROYECTO_DOCUMENTO_RESPONSE_CONVERTER = new ProyectoDocumentoResponseConverter();
