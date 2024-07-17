import { IRespuestaResponse } from '@core/services/eti/respuesta/respuesta-response';
import { IRespuesta } from '@core/models/eti/respuesta';
import { SgiBaseConverter } from '@sgi/framework/core';
import { APARTADO_RESPONSE_CONVERTER } from '../apartado/apartado-response.converter';
import { MEMORIA_RESPONSE_CONVERTER } from '../memoria/memoria-response.converter';


class RespuestaResponseConverter extends SgiBaseConverter<IRespuestaResponse, IRespuesta> {
  toTarget(value: IRespuestaResponse): IRespuesta {
    if (!value) {
      return value as unknown as IRespuesta;
    }
    return {
      id: value.id,
      memoria: MEMORIA_RESPONSE_CONVERTER.toTarget(value.memoria),
      apartado: APARTADO_RESPONSE_CONVERTER.toTarget(value.apartado),
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
      memoria: MEMORIA_RESPONSE_CONVERTER.fromTarget(value.memoria),
      apartado: APARTADO_RESPONSE_CONVERTER.fromTarget(value.apartado),
      tipoDocumento: value.tipoDocumento,
      valor: value.valor
    };
  }
}

export const RESPUESTA_RESPONSE_CONVERTER = new RespuestaResponseConverter();
