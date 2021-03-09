import { Estado } from '../estado-proyecto';

export interface IEstadoProyectoBackend {
  /** Id */
  id: number;
  /** Id del proyecto */
  idProyecto: number;
  /** Estado */
  estado: Estado;
  /** Fecha estado */
  fechaEstado: string;
  /** Comentario */
  comentario: string;
}
