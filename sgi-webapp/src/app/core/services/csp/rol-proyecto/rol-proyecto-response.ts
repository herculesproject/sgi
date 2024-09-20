import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { Orden, Equipo } from "@core/models/csp/rol-proyecto";

export interface IRolProyectoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  abreviatura: string;
  descripcion: string;
  rolPrincipal: boolean;
  baremablePRC: boolean;
  orden: Orden;
  equipo: Equipo;
  activo: boolean;
}
