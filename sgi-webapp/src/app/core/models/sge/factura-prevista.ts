import { DateTime } from "luxon";

export interface IFacturaPrevista {
  id: string;
  proyectoIdSGI: number;
  proyectoSgeRef: string;
  numeroPrevision: number;
  fechaEmision: DateTime;
  importeBase: number;
  porcentajeIVA: number;
  comentario: string;
  tipoFacturacion: string;
}
