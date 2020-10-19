import { Comite } from './comite';
import { IFormulario } from './formulario';

export interface IComiteFormulario {

  /** Id. */
  id: number;

  /** Comité. */
  comite: Comite;

  /** Formulario */
  formulario: IFormulario;


}
