import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IRolSocio {
  id: number;
  abreviatura: string;
  nombre: I18nFieldValue[];
  descripcion: string;
  coordinador: boolean;
  activo: boolean;
}
