import { IComite } from '@core/models/eti/comite';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IComiteResponse } from './comite-response';

class ComiteResponseConverter extends SgiBaseConverter<IComiteResponse, IComite> {
  toTarget(value: IComiteResponse): IComite {
    if (!value) {
      return value as unknown as IComite;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      nombre: value.nombre,
      genero: value.genero,
      formularioMemoriaId: value.formularioMemoriaId,
      formularioSeguimientoAnualId: value.formularioSeguimientoAnualId,
      formularioSeguimientoFinalId: value.formularioSeguimientoFinalId,
      formularioRetrospectivaId: value.formularioRetrospectivaId,
      requiereRetrospectiva: value.requiereRetrospectiva,
      prefijoReferencia: value.prefijoReferencia,
      permitirRatificacion: value.permitirRatificacion,
      tareaNombreLibre: value.tareaNombreLibre,
      tareaExperienciaLibre: value.tareaExperienciaLibre,
      tareaExperienciaDetalle: value.tareaExperienciaDetalle,
      memoriaTituloLibre: value.memoriaTituloLibre,
      activo: value.activo
    };
  }

  fromTarget(value: IComite): IComiteResponse {
    if (!value) {
      return value as unknown as IComiteResponse;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      nombre: value.nombre,
      genero: value.genero,
      formularioMemoriaId: value.formularioMemoriaId,
      formularioSeguimientoAnualId: value.formularioSeguimientoAnualId,
      formularioSeguimientoFinalId: value.formularioSeguimientoFinalId,
      formularioRetrospectivaId: value.formularioRetrospectivaId,
      requiereRetrospectiva: value.requiereRetrospectiva,
      prefijoReferencia: value.prefijoReferencia,
      permitirRatificacion: value.permitirRatificacion,
      tareaNombreLibre: value.tareaNombreLibre,
      tareaExperienciaLibre: value.tareaExperienciaLibre,
      tareaExperienciaDetalle: value.tareaExperienciaDetalle,
      memoriaTituloLibre: value.memoriaTituloLibre,
      activo: value.activo
    };
  }
}

export const COMITE_RESPONSE_CONVERTER = new ComiteResponseConverter();
