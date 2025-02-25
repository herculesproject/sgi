import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IAutorizacionResponse {
  id: number;
  observaciones: string;
  responsableRef: string;
  solicitanteRef: string;
  tituloProyecto: I18nFieldValueResponse[];
  entidadRef: string;
  horasDedicacion: number;
  datosResponsable: string;
  datosEntidad: string;
  datosConvocatoria: I18nFieldValueResponse[];
  convocatoriaId: number;
  estadoId: number;
}
