import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
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

  /** Definición del apartado */
  definicion: IApartadoDefinion[]
}

export interface IApartadoDefinion {
  /** Language */
  lang: string;

  /** Nombre */
  nombre: string;

  /** Esquema */
  esquema: SgiFormlyFieldConfig[];
}