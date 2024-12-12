import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IAreaTematica {
  id: number;
  nombre: I18nFieldValue[];
  descripcion: string;
  padre: IAreaTematica;
  activo: boolean;
}

