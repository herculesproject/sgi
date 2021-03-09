import { ITipoFase } from '../tipos-configuracion';
import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoPlazoBackend {
  /** Id */
  id: number;
  /** Proyecto */
  proyecto: IProyectoBackend;
  /** Tipo de hito */
  tipoFase: ITipoFase;
  /** Fecha inicio */
  fechaInicio: string;
  /** Fecha fin  */
  fechaFin: string;
  /** Observaciones */
  observaciones: string;
  /** Aviso */
  generaAviso: boolean;
}
