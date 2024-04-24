
export interface IFacturaPrevistaResponse {
  id: string;
  proyectoIdSGI: number;
  proyectoSgeRef: string;
  numeroPrevision: number;
  fechaEmision: string;
  importeBase: number;
  porcentajeIVA: number;
  comentario: string;
  tipoFacturacion: string;
}
