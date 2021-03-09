import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DateTime } from 'luxon';
import { IConvocatoria } from './convocatoria';

export interface IConvocatoriaPeriodoJustificacion {
  /** Id */
  id: number;
  /** Convocatoria */
  convocatoria: IConvocatoria;
  /** Num Periodo */
  numPeriodo: number;
  /** Mes inicial */
  mesInicial: number;
  /** Mes final */
  mesFinal: number;
  /** Fecha inicio */
  fechaInicioPresentacion: DateTime;
  /** Fecha fin */
  fechaFinPresentacion: DateTime;
  /** Observaciones */
  observaciones: string;
  /** Tipo */
  tipo: Tipo;
}

export enum Tipo {
  PERIODICO = 'PERIODICO',
  FINAL = 'FINAL'
}

export const TIPO_MAP: Map<Tipo, string> = new Map([
  [Tipo.PERIODICO, marker(`csp.convocatoria-periodo-justificacion.tipo.PERIODICO`)],
  [Tipo.FINAL, marker(`csp.convocatoria-periodo-justificacion.tipo.FINAL`)],
]);
