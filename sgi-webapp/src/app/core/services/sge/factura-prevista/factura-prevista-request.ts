
export interface IFacturaPrevistaRequest {
  proyectoIdSGI: number;
  proyectoSgeRef: string;
  numeroPrevision: number;
  fechaEmision: string;
  importeBase: number;
  porcentajeIVA: number;
  comentario: string;
  tipoFacturacion: string;
}
