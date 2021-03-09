import { DateTime } from 'luxon';
import { IProyecto } from './proyecto';
import { ITipoFase } from './tipos-configuracion';

export interface IProyectoPlazos {
  /** Id */
  id: number;
  /** Proyecto */
  proyecto: IProyecto;
  /** Tipo de hito */
  tipoFase: ITipoFase;
  /** Fecha inicio */
  fechaInicio: DateTime;
  /** Fecha fin  */
  fechaFin: DateTime;
  /** Observaciones */
  observaciones: string;
  /** Aviso */
  generaAviso: boolean;
}
