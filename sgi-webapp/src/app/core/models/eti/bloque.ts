import { IFormulario } from './formulario';

export interface IBloque {
  /** Id */
  id: number;

  /** Formulario */
  formulario: IFormulario;

  /** orden */
  orden: number;

  /** bloque language */
  nombre: IBloqueNombre[];
}

export interface IBloqueNombre {
  /** Language */
  lang: string;

  /** Nombre */
  value: string;
}