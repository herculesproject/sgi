import { IConvocatoriaBackend } from '@core/models/csp/backend/convocatoria-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico-response.converter';
import { TIPO_FINALIDAD_RESPONSE_CONVERTER } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response.converter';
import { TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER } from '@core/services/csp/tipo-regimen-concurrencia/tipo-regimen-concurrencia-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaConverter extends SgiBaseConverter<IConvocatoriaBackend, IConvocatoria> {

  toTarget(value: IConvocatoriaBackend): IConvocatoria {
    if (!value) {
      return value as unknown as IConvocatoria;
    }
    return {
      id: value.id,
      unidadGestion: { id: +value.unidadGestionRef } as IUnidadGestion,
      modeloEjecucion: value.modeloEjecucion,
      codigo: value.codigo,
      fechaPublicacion: LuxonUtils.fromBackend(value.fechaPublicacion),
      fechaProvisional: LuxonUtils.fromBackend(value.fechaProvisional),
      fechaConcesion: LuxonUtils.fromBackend(value.fechaConcesion),
      titulo: value.titulo,
      objeto: value.objeto,
      observaciones: value.observaciones,
      finalidad: TIPO_FINALIDAD_RESPONSE_CONVERTER.toTarget(value.finalidad),
      regimenConcurrencia: TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER.toTarget(value.regimenConcurrencia),
      formularioSolicitud: value.formularioSolicitud,
      estado: value.estado,
      duracion: value.duracion,
      abiertoPlazoPresentacionSolicitud: value.abiertoPlazoPresentacionSolicitud,
      ambitoGeografico: TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER.toTarget(value.ambitoGeografico),
      clasificacionCVN: value.clasificacionCVN,
      activo: value.activo,
      excelencia: value.excelencia,
      codigoInterno: value.codigoInterno,
      anio: value.anio
    };
  }

  fromTarget(value: IConvocatoria): IConvocatoriaBackend {
    if (!value) {
      return value as unknown as IConvocatoriaBackend;
    }
    return {
      id: value.id,
      unidadGestionRef: String(value.unidadGestion?.id),
      modeloEjecucion: value.modeloEjecucion,
      codigo: value.codigo,
      fechaPublicacion: LuxonUtils.toBackend(value.fechaPublicacion),
      fechaProvisional: LuxonUtils.toBackend(value.fechaProvisional),
      fechaConcesion: LuxonUtils.toBackend(value.fechaConcesion),
      titulo: value.titulo,
      objeto: value.objeto,
      observaciones: value.observaciones,
      finalidad: TIPO_FINALIDAD_RESPONSE_CONVERTER.fromTarget(value.finalidad),
      regimenConcurrencia: TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER.fromTarget(value.regimenConcurrencia),
      formularioSolicitud: value.formularioSolicitud,
      estado: value.estado,
      duracion: value.duracion,
      abiertoPlazoPresentacionSolicitud: value.abiertoPlazoPresentacionSolicitud,
      ambitoGeografico: TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER.fromTarget(value.ambitoGeografico),
      clasificacionCVN: value.clasificacionCVN,
      activo: value.activo,
      excelencia: value.excelencia,
      codigoInterno: value.codigoInterno,
      anio: value.anio
    };
  }
}

export const CONVOCATORIA_CONVERTER = new ConvocatoriaConverter();
