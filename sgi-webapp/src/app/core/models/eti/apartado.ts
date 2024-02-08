import { IApartadoNombre } from './apartado-nombre';
import { IBloque } from './bloque';

export interface IApartado {
  /** Id */
  id: number;

  /** Bloque del apartado */
  bloque: IBloque;

  /** Apartado padre del formulario */
  padre: IApartado;

  /** Orden */
  orden: number;

  apartadoNombres: IApartadoNombre[]
}
