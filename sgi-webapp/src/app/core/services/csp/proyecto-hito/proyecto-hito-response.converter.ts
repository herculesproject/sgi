import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ITipoHitoResponse } from '../tipo-hito/tipo-hito-response';
import { TIPO_HITO_RESPONSE_CONVERTER } from '../tipo-hito/tipo-hito-response.converter';
import { IProyectoHitoResponse } from './proyecto-hito-response';

class ProyectoHitoResponseConverter extends SgiBaseConverter<IProyectoHitoResponse, IProyectoHito> {

  toTarget(value: IProyectoHitoResponse): IProyectoHito {
    if (!value) {
      return value as unknown as IProyectoHito;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.fromBackend(value.fecha),
      tipoHito: value.tipoHito ? TIPO_HITO_RESPONSE_CONVERTER.toTarget(value.tipoHito) : null,
      comentario: value.comentario,
      proyectoId: value.proyectoId,
      aviso: value.proyectoHitoAviso ? {
        email: {
          id: Number(value.proyectoHitoAviso.comunicadoRef)
        } as IGenericEmailText,
        task: {
          id: Number(value.proyectoHitoAviso.tareaProgramadaRef)
        } as ISendEmailTask,
        incluirIpsProyecto: value.proyectoHitoAviso.incluirIpsProyecto,
      } : null
    };
  }

  fromTarget(value: IProyectoHito): IProyectoHitoResponse {
    if (!value) {
      return value as unknown as IProyectoHitoResponse;
    }
    return {
      id: value.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      tipoHito: {
        id: value.tipoHito.id
      } as ITipoHitoResponse,
      comentario: value.comentario,
      proyectoId: value.proyectoId,
      proyectoHitoAviso: value.aviso ? {
        comunicadoRef: value.aviso.email.id.toString(),
        tareaProgramadaRef: value.aviso.task.id.toString(),
        incluirIpsProyecto: value.aviso.incluirIpsProyecto,
      } : null
    };
  }
}

export const PROYECTO_HITO_RESPONSE_CONVERTER = new ProyectoHitoResponseConverter();
