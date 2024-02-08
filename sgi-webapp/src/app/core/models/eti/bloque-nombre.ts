import { IBloque } from "./bloque";

export interface IBloqueNombre {
  /** identificador */
  id: number;

  /** Nombre */
  nombre: string;

  /** bloque */
  bloque: IBloque;

  /** Language */
  lang: string;
}
