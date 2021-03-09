import { IConvocatoriaConceptoGasto } from '../convocatoria-concepto-gasto';

export interface IConvocatoriaConceptoGastoCodigoEcBackend {
  /** id */
  id: number;
  /** ConceptoGasto */
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
  /** Referencia código económico */
  codigoEconomicoRef: string;
  /** Fecha inicio */
  fechaInicio: string;
  /** Fecha fin */
  fechaFin: string;
  /** Observaciones */
  observaciones: string;
}
