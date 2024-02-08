import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { IBloque } from './bloque';
import { IBloqueOutput } from './bloque-output';

export interface IApartadoOutput {
  /** Id */
  id: number;

  /** Bloque del apartado */
  bloque: IBloqueOutput;

  /** Nombre */
  nombre: string;

  /** Apartado padre del formulario */
  padre: IApartadoOutput;

  /** Orden */
  orden: number;

  /** Esquema */
  esquema: SgiFormlyFieldConfig[];

  /** Language */
  lang: string;
}
