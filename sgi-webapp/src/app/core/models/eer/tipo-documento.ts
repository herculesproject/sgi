import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface ITipoDocumento {
  id: number;
  nombre: I18nFieldValue[];
  descripcion: string;
  activo: boolean;
}
