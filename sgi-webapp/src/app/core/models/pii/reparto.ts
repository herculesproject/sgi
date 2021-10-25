import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IInvencion } from './invencion';

export interface IReparto {
  id: number;
  invencion: IInvencion;
  fecha: DateTime;
  importeUniversidad: number;
  estado: Estado;
}

export enum Estado {
  PENDIENTE_EJECUTAR = 'PENDIENTE_EJECUTAR',
  EJECUTADO = 'EJECUTADO'
}

export const ESTADO_MAP: Map<Estado, string> = new Map([
  [Estado.PENDIENTE_EJECUTAR, marker('pii.invencion-reparto.estado.PENDIENTE_EJECUTAR')],
  [Estado.EJECUTADO, marker('pii.invencion-reparto.estado.EJECUTADO')],
]);
