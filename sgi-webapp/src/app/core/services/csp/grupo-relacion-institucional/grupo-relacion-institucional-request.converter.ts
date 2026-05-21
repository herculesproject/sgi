import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoRelacionInstitucional } from '@core/models/csp/grupo-relacion-institucional';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoRelacionInstitucionalRequest } from './grupo-relacion-institucional-request';

class GrupoRelacionInstitucionalRequestConverter
  extends SgiBaseConverter<IGrupoRelacionInstitucionalRequest, IGrupoRelacionInstitucional> {
  toTarget(value: IGrupoRelacionInstitucionalRequest): IGrupoRelacionInstitucional {
    if (!value) {
      return value as unknown as IGrupoRelacionInstitucional;
    }
    return {
      id: undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      entidad: value.entidadRef ? { id: value.entidadRef } as IEmpresa : undefined,
      institucion: value.institucion
    };
  }

  fromTarget(value: IGrupoRelacionInstitucional): IGrupoRelacionInstitucionalRequest {
    if (!value) {
      return value as unknown as IGrupoRelacionInstitucionalRequest;
    }
    return {
      grupoId: value.grupo.id,
      entidadRef: value.entidad?.id,
      institucion: value.institucion
    };
  }
}

export const GRUPO_RELACION_INSTITUCIONAL_REQUEST_CONVERTER = new GrupoRelacionInstitucionalRequestConverter();
