import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { ITipoEnlace } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@herculesproject/framework/core';
import { IGrupoEnlaceResponse } from './grupo-enlace-response';

class GrupoEnlaceResponseConverter
  extends SgiBaseConverter<IGrupoEnlaceResponse, IGrupoEnlace> {
  toTarget(value: IGrupoEnlaceResponse): IGrupoEnlace {
    if (!value) {
      return value as unknown as IGrupoEnlace;
    }
    return {
      id: value.id,
      grupo: value.grupoId ? { id: value.grupoId } as IGrupo : undefined,
      enlace: value.enlace,
      tipoEnlace: value.tipoEnlaceId ? { id: value.tipoEnlaceId } as ITipoEnlace : undefined
    };
  }

  fromTarget(value: IGrupoEnlace): IGrupoEnlaceResponse {
    if (!value) {
      return value as unknown as IGrupoEnlaceResponse;
    }
    return {
      id: value.id,
      grupoId: value.grupo.id,
      enlace: value.enlace,
      tipoEnlaceId: value.tipoEnlace?.id
    };
  }
}

export const GRUPO_ENLACE_RESPONSE_CONVERTER = new GrupoEnlaceResponseConverter();
