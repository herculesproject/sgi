import { DateTime } from 'luxon';
import { IConvocatoria } from './convocatoria';
import { ITipoFase } from './tipos-configuracion';

export interface IConvocatoriaFase {
  id: number;
  convocatoria: IConvocatoria;
  tipoFase: ITipoFase;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  observaciones: string;
}
