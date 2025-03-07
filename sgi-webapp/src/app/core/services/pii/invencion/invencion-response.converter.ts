import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IInvencionResponse } from './invencion-response';
import { TIPO_PROTECCION_RESPONSE_CONVERTER } from '../tipo-proteccion/tipo-proteccion-response.converter';

class InvencionResponseConverter extends SgiBaseConverter<IInvencionResponse, IInvencion> {
  toTarget(value: IInvencionResponse): IInvencion {
    if (!value) {
      return value as unknown as IInvencion;
    }
    return {
      id: value.id,
      fechaComunicacion: LuxonUtils.fromBackend(value.fechaComunicacion),
      titulo: value.titulo,
      descripcion: value.descripcion,
      comentarios: value.comentarios,
      proyecto: value.proyectoRef !== null ? { id: +value.proyectoRef } as IProyecto : null,
      tipoProteccion: value.tipoProteccion ? TIPO_PROTECCION_RESPONSE_CONVERTER.toTarget(value.tipoProteccion) : null,
      activo: value.activo
    };
  }
  fromTarget(value: IInvencion): IInvencionResponse {
    if (!value) {
      return value as unknown as IInvencionResponse;
    }
    return {
      id: value.id,
      fechaComunicacion: LuxonUtils.toBackend(value.fechaComunicacion),
      titulo: value.titulo,
      descripcion: value.descripcion,
      comentarios: value.comentarios,
      proyectoRef: value.proyecto?.id?.toString(),
      tipoProteccion: value.tipoProteccion ? TIPO_PROTECCION_RESPONSE_CONVERTER.fromTarget(value.tipoProteccion) : null,
      activo: value.activo
    };
  }
}

export const INVENCION_RESPONSE_CONVERTER = new InvencionResponseConverter();
