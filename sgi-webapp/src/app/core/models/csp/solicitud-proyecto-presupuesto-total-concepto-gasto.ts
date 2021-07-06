import { IConceptoGasto } from './tipos-configuracion';

export interface ISolicitudProyectoPresupuestoTotalConceptoGasto {
  conceptoGasto: IConceptoGasto;
  importeTotalSolicitado: number;
  importeTotalPresupuestado: number;
}
