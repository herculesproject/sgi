import { IFormulario } from './formulario';

export interface IBloqueFormulario {
  /** Id */
  id: number;

  /** Formulario */
  formulario: IFormulario;

  /** Nombre */
  nombre: string;

  /** Orden */
  orden: number;

  /** Activo */
  activo: boolean;
}
