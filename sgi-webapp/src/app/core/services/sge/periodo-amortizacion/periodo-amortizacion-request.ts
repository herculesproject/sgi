import { I18nFieldValue } from "@core/i18n/i18n-field";

export interface IPeriodoAmortizacionRequest {
  id: string;
  proyectoId: string;
  anualidad: string;
  empresaRef: string;
  tipoFinanciacion: {
    id: string,
    nombre: I18nFieldValue[]
  },
  fuenteFinanciacion: {
    id: string,
    nombre: I18nFieldValue[]
  },
  fecha: string;
  importe: number;
}