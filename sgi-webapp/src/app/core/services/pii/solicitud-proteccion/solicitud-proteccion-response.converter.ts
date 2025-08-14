import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPais } from '@core/models/sgo/pais';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { INVENCION_RESPONSE_CONVERTER } from '../invencion/invencion-response.converter';
import { TIPO_CADUCIDAD_RESPONSE_CONVERTER } from '../tipo-caducidad/tipo-caducidad-response.converter';
import { VIA_PROTECCION_RESPONSE_CONVERTER } from '../via-proteccion/via-proteccion-response.converter';
import { ISolicitudProteccionResponse } from './solicitud-proteccion-response';

export class SolicitudProteccionResponseConverter extends SgiBaseConverter<ISolicitudProteccionResponse, ISolicitudProteccion> {

  toTarget(value: ISolicitudProteccionResponse): ISolicitudProteccion {
    return value ? {
      id: value.id,
      fechaCaducidad: LuxonUtils.fromBackend(value.fechaCaducidad),
      fechaConcesion: LuxonUtils.fromBackend(value.fechaConcesion),
      fechaFinPriorPresFasNacRec: LuxonUtils.fromBackend(value.fechaFinPriorPresFasNacRec),
      fechaPrioridadSolicitud: LuxonUtils.fromBackend(value.fechaPrioridadSolicitud),
      fechaPublicacion: LuxonUtils.fromBackend(value.fechaPublicacion),
      agentePropiedad: { id: value.agentePropiedadRef } as IEmpresa,
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentarios) : [],
      estado: value.estado,
      invencion: value.invencion ? INVENCION_RESPONSE_CONVERTER.toTarget(value.invencion) : null,
      numeroConcesion: value.numeroConcesion,
      numeroPublicacion: value.numeroPublicacion,
      numeroRegistro: value.numeroRegistro,
      numeroSolicitud: value.numeroSolicitud,
      paisProteccion: { id: value.paisProteccionRef } as IPais,
      tipoCaducidad: value.tipoCaducidad ? TIPO_CADUCIDAD_RESPONSE_CONVERTER.toTarget(value.tipoCaducidad) : null,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      viaProteccion: value.viaProteccion ? VIA_PROTECCION_RESPONSE_CONVERTER.toTarget(value.viaProteccion) : null,
    } : (value as unknown as ISolicitudProteccion);
  }

  fromTarget(value: ISolicitudProteccion): ISolicitudProteccionResponse {
    return value ? {
      id: value.id,
      fechaCaducidad: LuxonUtils.toBackend(value.fechaCaducidad),
      fechaConcesion: LuxonUtils.toBackend(value.fechaConcesion),
      fechaFinPriorPresFasNacRec: LuxonUtils.toBackend(value.fechaFinPriorPresFasNacRec),
      fechaPrioridadSolicitud: LuxonUtils.toBackend(value.fechaPrioridadSolicitud),
      fechaPublicacion: LuxonUtils.toBackend(value.fechaPublicacion),
      agentePropiedadRef: value.agentePropiedad.id,
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentarios) : [],
      estado: value.estado,
      invencion: value.invencion ? INVENCION_RESPONSE_CONVERTER.fromTarget(value.invencion) : null,
      numeroConcesion: value.numeroConcesion,
      numeroPublicacion: value.numeroPublicacion,
      numeroRegistro: value.numeroRegistro,
      numeroSolicitud: value.numeroSolicitud,
      paisProteccionRef: value.paisProteccion.id,
      tipoCaducidad: value.tipoCaducidad ? TIPO_CADUCIDAD_RESPONSE_CONVERTER.fromTarget(value.tipoCaducidad) : null,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      viaProteccion: value.viaProteccion ? VIA_PROTECCION_RESPONSE_CONVERTER.fromTarget(value.viaProteccion) : null,
    } : (value as unknown as ISolicitudProteccionResponse);
  }
}

export const SOLICITUD_PROTECCION_RESPONSE_CONVERTER = new SolicitudProteccionResponseConverter();
