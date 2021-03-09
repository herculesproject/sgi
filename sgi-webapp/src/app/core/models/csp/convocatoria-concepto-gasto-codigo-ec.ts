import { DateTime } from 'luxon';
import { IConvocatoriaConceptoGasto } from './convocatoria-concepto-gasto';

export interface IConvocatoriaConceptoGastoCodigoEc {
  /** id */
  id: number;
  /** ConceptoGasto */
  convocatoriaConceptoGasto: IConvocatoriaConceptoGasto;
  /** Referencia código económico */
  codigoEconomicoRef: string;
  /** Fecha inicio */
  fechaInicio: DateTime;
  /** Fecha fin */
  fechaFin: DateTime;
  /** Observaciones */
  observaciones: string;
}
