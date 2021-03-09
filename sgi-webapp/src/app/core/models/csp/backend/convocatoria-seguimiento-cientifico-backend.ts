import { IConvocatoria } from '../convocatoria';

export interface IConvocatoriaSeguimientoCientificoBackend {
  id: number;
  numPeriodo: number;
  mesInicial: number;
  mesFinal: number;
  fechaInicioPresentacion: string;
  fechaFinPresentacion: string;
  observaciones: string;
  convocatoria: IConvocatoria;
}
