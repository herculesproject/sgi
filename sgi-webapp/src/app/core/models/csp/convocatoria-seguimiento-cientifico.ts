import { DateTime } from 'luxon';
import { IConvocatoria } from './convocatoria';

export interface IConvocatoriaSeguimientoCientifico {
  id: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicioPresentacion: DateTime;
  fechaFinPresentacion: DateTime;
  observaciones: string;
  convocatoria: IConvocatoria;
}
