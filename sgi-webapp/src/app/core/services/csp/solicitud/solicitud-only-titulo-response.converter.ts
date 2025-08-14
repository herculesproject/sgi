import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISolicitud } from '@core/models/csp/solicitud';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { ISolicitudOnlyTituloResponse } from './solicitud-only-titulo-response';

class SolicitudOnlyTituloResponseConverter extends SgiBaseConverter<ISolicitudOnlyTituloResponse, ISolicitud> {

  toTarget(value: ISolicitudOnlyTituloResponse): ISolicitud {
    if (!value) {
      return value as unknown as ISolicitud;
    }
    return {
      id: value.id,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      activo: undefined,
      codigoExterno: undefined,
      codigoRegistroInterno: undefined,
      estado: undefined,
      convocatoriaId: undefined,
      convocatoriaExterna: undefined,
      creador: undefined,
      solicitante: undefined,
      formularioSolicitud: undefined,
      tipoSolicitudGrupo: undefined,
      unidadGestion: undefined,
      observaciones: undefined,
      anio: undefined,
      modeloEjecucion: undefined,
      origenSolicitud: undefined,
      tipoFinalidad: undefined
    };
  }

  fromTarget(value: ISolicitud): ISolicitudOnlyTituloResponse {
    if (!value) {
      return value as unknown as ISolicitudOnlyTituloResponse;
    }
    return {
      id: value.id,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
    };
  }

}

export const SOLICITUD_ONLY_TITULO_RESPONSE_CONVERTER = new SolicitudOnlyTituloResponseConverter();
