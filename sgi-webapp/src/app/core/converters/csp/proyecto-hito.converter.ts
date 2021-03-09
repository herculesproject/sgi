import { IProyectoHitoBackend } from '@core/models/csp/backend/proyecto-hito-backend';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_CONVERTER } from './proyecto.converter';

class ProyectoHitoConverter extends SgiBaseConverter<IProyectoHitoBackend, IProyectoHito> {

  toTarget(value: IProyectoHitoBackend): IProyectoHito {
    if (!value) {
      return value as unknown as IProyectoHito;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.toTarget(value.proyecto),
      tipoHito: value.tipoHito,
      fecha: LuxonUtils.fromBackend(value.fecha),
      comentario: value.comentario,
      generaAviso: value.generaAviso
    };
  }

  fromTarget(value: IProyectoHito): IProyectoHitoBackend {
    if (!value) {
      return value as unknown as IProyectoHitoBackend;
    }
    return {
      id: value.id,
      proyecto: PROYECTO_CONVERTER.fromTarget(value.proyecto),
      tipoHito: value.tipoHito,
      fecha: LuxonUtils.toBackend(value.fecha),
      comentario: value.comentario,
      generaAviso: value.generaAviso
    };
  }
}

export const PROYECTO_HITO_CONVERTER = new ProyectoHitoConverter();
