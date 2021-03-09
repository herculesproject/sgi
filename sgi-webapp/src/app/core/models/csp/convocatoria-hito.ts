import { DateTime } from 'luxon';
import { IConvocatoria } from './convocatoria';
import { ITipoHito } from './tipos-configuracion';

export interface IConvocatoriaHito {
  /** Id */
  id: number;
  /** Fecha inicio  */
  fecha: DateTime;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Comentario */
  comentario: string;
  /** convocatoria */
  convocatoria: IConvocatoria;
  /** Aviso */
  generaAviso: boolean;
}
