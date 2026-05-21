import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoRelacionInstitucionalResponse } from './grupo-relacion-institucional-response';

class GrupoRelacionInstitucionalResponseConverter
  extends SgiBaseConverter<IGrupoRelacionInstitucionalResponse, IGrupoRelacionInstitucional> {
  toTarget(value: IGrupoRelacionInstitucionalResponse): IGrupoRelacionInstitucional {
    if (!value) {
      return value as unknown as IGrupoRelacionInstitucional;
    }
    return {
      id: value.id,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      entidad: value.entidadRef ? { id: value.entidadRef } as IEmpresa : undefined,
      institucion: value.institucion
    };
  }

  fromTarget(value: IGrupoRelacionInstitucional): IGrupoRelacionInstitucionalResponse {
    if (!value) {
      return value as unknown as IGrupoRelacionInstitucionalResponse;
    }
    return {
      id: value.id,
      grupoId: value.grupo.id,
      entidadRef: value.entidad?.id,
      institucion: value.institucion
    };
  }
}

export const GRUPO_RELACION_INSTITUCIONAL_RESPONSE_CONVERTER = new GrupoRelacionInstitucionalResponseConverter();
