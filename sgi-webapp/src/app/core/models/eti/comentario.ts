import { ApartadoFormulario } from './apartado-formulario';
import { IEvaluacion } from './evaluacion';
import { TipoComentario } from './tipo-comentario';

export class Comentario {
  /** Id */
  id: number;

  /** Apartado del formulario */
  apartadoFormulario: ApartadoFormulario;

  /** Evaluación */
  evaluacion: IEvaluacion;

  /** Tipo de comentario */
  tipoComentario: TipoComentario;

  /** Texto */
  texto: string;

  constructor(comentario?: Comentario) {
    this.id = comentario?.id;
    this.apartadoFormulario = comentario?.apartadoFormulario;
    this.evaluacion = comentario?.evaluacion;
    this.tipoComentario = comentario?.tipoComentario;
    this.texto = comentario?.texto;
  }
}
