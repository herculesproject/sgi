import { IFormulario } from './formulario';

export interface IBloqueOutput {
  /** Id */
  id: number;

  /** Formulario */
  formulario: IFormulario;

  /** Nombre */
  nombre: string;

  /** Orden */
  orden: number;

  /** Language */
  lang: string;

}
