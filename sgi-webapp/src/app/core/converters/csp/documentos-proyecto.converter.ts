import { IDocumentosProyectoBackend } from '@core/models/csp/backend/documentos-proyecto-backend';
import { IDocumentosProyecto } from '@core/models/csp/documentos-proyecto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_DOCUMENTO_CONVERTER } from './proyecto-documento.converter';
import { PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER } from './proyecto-periodo-seguimiento-documento.converter';
import { PROYECTO_PRORROGA_DOCUMENTO_CONVERTER } from './proyecto-prorroga-documento.converter';
import { SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER } from './socio-periodo-justificacion-documento.converter';

class DocumentosProyectoConverter extends SgiBaseConverter<IDocumentosProyectoBackend, IDocumentosProyecto> {

  toTarget(value: IDocumentosProyectoBackend): IDocumentosProyecto {
    if (!value) {
      return value as unknown as IDocumentosProyecto;
    }
    return {
      proyectoDocumentos: PROYECTO_DOCUMENTO_CONVERTER.toTargetArray(value.proyectoDocumentos),
      socioPeriodoJustificacionDocumentos: SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER
        .toTargetArray(value.socioPeriodoJustificacionDocumentos),
      proyectoPeriodoSeguimientoDocumentos: PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER
        .toTargetArray(value.proyectoPeriodoSeguimientoDocumentos),
      prorrogaDocumentos: PROYECTO_PRORROGA_DOCUMENTO_CONVERTER.toTargetArray(value.prorrogaDocumentos)
    };
  }

  fromTarget(value: IDocumentosProyecto): IDocumentosProyectoBackend {
    if (!value) {
      return value as unknown as IDocumentosProyectoBackend;
    }
    return {
      proyectoDocumentos: PROYECTO_DOCUMENTO_CONVERTER.fromTargetArray(value.proyectoDocumentos),
      socioPeriodoJustificacionDocumentos: SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_CONVERTER
        .fromTargetArray(value.socioPeriodoJustificacionDocumentos),
      proyectoPeriodoSeguimientoDocumentos: PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER
        .fromTargetArray(value.proyectoPeriodoSeguimientoDocumentos),
      prorrogaDocumentos: PROYECTO_PRORROGA_DOCUMENTO_CONVERTER.fromTargetArray(value.prorrogaDocumentos)
    };
  }
}

export const DOCUMENTOS_PROYECTO_CONVERTER = new DocumentosProyectoConverter();
