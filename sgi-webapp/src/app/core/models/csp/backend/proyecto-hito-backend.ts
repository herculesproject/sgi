import { ITipoHito } from '../tipos-configuracion';
import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoHitoBackend {
  /** Id */
  id: number;
  /** Proyecto */
  proyecto: IProyectoBackend;
  /** Tipo de hito */
  tipoHito: ITipoHito;
  /** Fecha  */
  fecha: string;
  /** Comentario */
  comentario: string;
  /** Aviso */
  generaAviso: boolean;
}
