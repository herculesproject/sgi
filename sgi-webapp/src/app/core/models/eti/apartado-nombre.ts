import { SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';

export interface IApartadoNombre {
  /** Bloque del formulario */
  apartadoId: number;

  /** Nombre */
  nombre: string;

  /** Esquema */
  esquema: SgiFormlyFieldConfig[];

  /** Language */
  lang: string;
}
