import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IResultadoInformePatentibilidad {
  /** id */
  id: number;

  /** Nombre */
  nombre: I18nFieldValue[];

  /** Nombre */
  descripcion: string;

  /** Activo */
  activo: boolean;
}
