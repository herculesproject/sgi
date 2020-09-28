import { IApartadoFormulario } from './apartado-formulario';
import { IEvaluacion } from './evaluacion';
import { TipoComentario } from './tipo-comentario';

export interface IComentario {
  /** Id */
  id: number;

  /** Apartado del formulario */
  apartadoFormulario: IApartadoFormulario;

  /** Evaluación */
  evaluacion: IEvaluacion;

  /** Tipo de comentario */
  tipoComentario: TipoComentario;

  /** Texto */
  texto: string;
}
