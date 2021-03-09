import { ISocioPeriodoJustificacionDocumentoBackend } from '@core/models/csp/backend/socio-periodo-justificacion-documento-backend';
import { ISocioPeriodoJustificacionDocumento } from '@core/models/csp/socio-periodo-justificacion-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER } from './proyecto-socio-periodo-justificacion.converter';

class SocioPeriodoJustificacionDocumentoConverter extends
  SgiBaseConverter<ISocioPeriodoJustificacionDocumentoBackend, ISocioPeriodoJustificacionDocumento> {

  toTarget(value: ISocioPeriodoJustificacionDocumentoBackend): ISocioPeriodoJustificacionDocumento {
    if (!value) {
      return value as unknown as ISocioPeriodoJustificacionDocumento;
    }
    return {
      id: value.id,
      proyectoSocioPeriodoJustificacion: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER.toTarget(value.proyectoSocioPeriodoJustificacion),
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }

  fromTarget(value: ISocioPeriodoJustificacionDocumento): ISocioPeriodoJustificacionDocumentoBackend {
    if (!value) {
      return value as unknown as ISocioPeriodoJustificacionDocumentoBackend;
    }
    return {
      id: value.id,
      proyectoSocioPeriodoJustificacion: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_CONVERTER.fromTarget(value.proyectoSocioPeriodoJustificacion),
      nombre: value.nombre,
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento,
      comentario: value.comentario,
      visible: value.visible
    };
  }
}

export const SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER = new SocioPeriodoJustificacionDocumentoConverter();
