import { IRespuesta } from '@core/models/eti/respuesta';
import { IRespuestaResponse } from '@core/services/eti/respuesta/respuesta-response';
import { SgiBaseConverter } from '@sgi/framework/core';


class RespuestaResponseConverter extends SgiBaseConverter<IRespuestaResponse, IRespuesta> {
  toTarget(value: IRespuestaResponse): IRespuesta {
    if (!value) {
      return value as unknown as IRespuesta;
    }
    return {
      id: value.id,
      memoriaId: value.memoriaId,
      apartadoId: value.apartadoId,
      tipoDocumento: value.tipoDocumento,
      valor: value.valor
    };
  }

  fromTarget(value: IRespuesta): IRespuestaResponse {
    if (!value) {
      return value as unknown as IRespuestaResponse;
    }
    return {
      id: value.id,
      memoriaId: value.memoriaId,
      apartadoId: value.apartadoId,
      tipoDocumento: value.tipoDocumento,
      valor: value.valor
    };
  }
}

export const RESPUESTA_RESPONSE_CONVERTER = new RespuestaResponseConverter();
