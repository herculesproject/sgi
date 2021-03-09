import { IProyectoProrrogaDocumentoBackend } from '@core/models/csp/backend/proyecto-prorroga-documento-backend';
import { IProyectoProrrogaDocumento } from '@core/models/csp/proyecto-prorroga-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_PRORROGA_CONVERTER } from './proyecto-prorroga.converter';

class ProyectoProrrogaDocumentoConverter extends SgiBaseConverter<IProyectoProrrogaDocumentoBackend, IProyectoProrrogaDocumento> {

  toTarget(value: IProyectoProrrogaDocumentoBackend): IProyectoProrrogaDocumento {
    if (!value) {
      return value as unknown as IProyectoProrrogaDocumento;
    }
    return {
      id: value.id,
      proyectoProrroga: PROYECTO_PRORROGA_CONVERTER.toTarget(value.proyectoProrroga),
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      visible: value.visible,
      comentario: value.comentario
    };
  }

  fromTarget(value: IProyectoProrrogaDocumento): IProyectoProrrogaDocumentoBackend {
    if (!value) {
      return value as unknown as IProyectoProrrogaDocumentoBackend;
    }
    return {
      id: value.id,
      proyectoProrroga: PROYECTO_PRORROGA_CONVERTER.fromTarget(value.proyectoProrroga),
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      visible: value.visible,
      comentario: value.comentario
    };
  }
}

export const PROYECTO_PRORROGA_DOCUMENTO_CONVERTER = new ProyectoProrrogaDocumentoConverter();
