import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IGrupo } from '@core/models/csp/grupo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { TIPO_GRUPO_RESPONSE_CONVERTER } from '../tipo-grupo/tipo-grupo-response.converter';
import { IGrupoResponse } from './grupo-response';

class GrupoResponseConverter
  extends SgiBaseConverter<IGrupoResponse, IGrupo> {
  toTarget(value: IGrupoResponse): IGrupo {
    if (!value) {
      return value as unknown as IGrupo;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      direccion: value.direccion,
      email: value.email,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      imagenRef: value.imagenRef,
      proyectoSge: value.proyectoSgeRef ? { id: value.proyectoSgeRef } as IProyectoSge : null,
      solicitud: value.solicitudId ? { id: value.solicitudId } as ISolicitud : null,
      codigo: value.codigo,
      tipoGrupo: value.tipoGrupo ? TIPO_GRUPO_RESPONSE_CONVERTER.toTarget(value.tipoGrupo) : null,
      especialInvestigacion: value.especialInvestigacion,
      departamentoOrigenRef: null,
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.resumen) : [],
      activo: value.activo
    };
  }

  fromTarget(value: IGrupo): IGrupoResponse {
    if (!value) {
      return value as unknown as IGrupoResponse;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      direccion: value.direccion,
      email: value.email,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      imagenRef: value.imagenRef,
      proyectoSgeRef: value.proyectoSge?.id,
      solicitudId: value.solicitud?.id,
      codigo: value.codigo,
      tipoGrupo: value.tipoGrupo ? TIPO_GRUPO_RESPONSE_CONVERTER.fromTarget(value.tipoGrupo) : null,
      especialInvestigacion: value.especialInvestigacion,
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.resumen) : [],
      activo: value.activo
    };
  }
}

export const GRUPO_RESPONSE_CONVERTER = new GrupoResponseConverter();
