import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoEnlaceRequest } from './grupo-enlace-request';

class GrupoEnlaceRequestConverter
  extends SgiBaseConverter<IGrupoEnlaceRequest, IGrupoEnlace> {
  toTarget(value: IGrupoEnlaceRequest): IGrupoEnlace {
    if (!value) {
      return value as unknown as IGrupoEnlace;
    }
    return {
      id: undefined,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      enlace: value.enlace,
      tipoEnlace: value.tipoEnlaceId ? { id: value.tipoEnlaceId } as ITipoEnlace : undefined
    };
  }

  fromTarget(value: IGrupoEnlace): IGrupoEnlaceRequest {
    if (!value) {
      return value as unknown as IGrupoEnlaceRequest;
    }
    return {
      grupoId: value.grupo.id,
      enlace: value.enlace,
      tipoEnlaceId: value.tipoEnlace?.id
    };
  }
}

export const GRUPO_ENLACE_REQUEST_CONVERTER = new GrupoEnlaceRequestConverter();
