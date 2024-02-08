import { IBloqueNombre } from './bloque-nombre';
import { IFormulario } from './formulario';

export interface IBloque {
  /** Id */
  id: number;

  /** Formulario */
  formulario: IFormulario;

  /** orden */
  orden: number;

  /** bloque language */
  bloqueNombres: IBloqueNombre[];
}
