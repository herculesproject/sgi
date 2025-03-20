import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IEmpresaDocumentoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  comentarios: string;
  empresaId: number;
  tipoDocumento: {
    id: number;
    nombre: string;
    descripcion: string;
    padre: {
      id: number;
      nombre: string;
      descripcion: string;
      activo: boolean;
    };
    activo: boolean;
  };
}
