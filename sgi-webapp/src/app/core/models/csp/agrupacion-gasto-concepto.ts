import { IConceptoGasto } from './tipos-configuracion';

export interface IAgrupacionGastoConcepto {
  /** Id */
  id: number;
  /** Proyecto */
  agrupacionId: number;
  /** Nombre */
  conceptoGasto: IConceptoGasto;
}
