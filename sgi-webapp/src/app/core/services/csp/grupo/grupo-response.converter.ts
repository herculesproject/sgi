import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEspecialInvestigacion } from '@core/models/csp/grupo-especial-investigacion';
import { IGrupoTipo } from '@core/models/csp/grupo-tipo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IGrupoResponse } from './grupo-response';

class GrupoResponseConverter
  extends SgiBaseConverter<IGrupoResponse, IGrupo> {
  toTarget(value: IGrupoResponse): IGrupo {
    if (!value) {
      return value as unknown as IGrupo;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      proyectoSge: value.proyectoSgeRef ? { id: value.proyectoSgeRef } as IProyectoSge : undefined,
      solicitud: value.solicitudId ? { id: value.solicitudId } as ISolicitud : undefined,
      codigo: value.codigo,
      tipo: value.tipo ? value.tipo as IGrupoTipo : undefined,
      especialInvestigacion: value.especialInvestigacionId ? { id: value.especialInvestigacionId } as IGrupoEspecialInvestigacion : undefined,
      activo: value.activo
    };
  }

  fromTarget(value: IGrupo): IGrupoResponse {
    if (!value) {
      return value as unknown as IGrupoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      proyectoSgeRef: value.proyectoSge?.id,
      solicitudId: value.solicitud?.id,
      codigo: value.codigo,
      tipo: value.tipo,
      especialInvestigacionId: value.especialInvestigacion?.id,
      activo: value.activo
    };
  }
}

export const GRUPO_RESPONSE_CONVERTER = new GrupoResponseConverter();
