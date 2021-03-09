import { DateTime } from 'luxon';
import { IProyecto } from './proyecto';
import { ITipoHito } from './tipos-configuracion';

export interface IProyectoHito {
  /** Id */
  id: number;
  /** Proyecto */
  proyecto: IProyecto;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Fecha  */
  fecha: DateTime;
  /** Comentario */
  comentario: string;
  /** Aviso */
  generaAviso: boolean;
}
