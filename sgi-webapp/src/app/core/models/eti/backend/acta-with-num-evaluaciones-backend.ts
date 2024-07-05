import { IActaDocumento } from '../acta-documento';
import { TipoConvocatoriaReunion } from '../tipo-convocatoria-reunion';
import { TipoEstadoActa } from '../tipo-estado-acta';

export interface IActaWithNumEvaluacionesBackend {
  /** ID */
  id: number;
  /** Comite */
  comite: string;
  /** Fecha evaluación */
  fechaEvaluacion: string;
  /** Numero acta */
  numeroActa: number;
  /** Tipo Convocatoria */
  tipoConvocatoria: TipoConvocatoriaReunion;
  /** Nº de evaluaciones (iniciales) */
  numEvaluaciones: number;
  /** Nº de revisiones */
  numRevisiones: number;
  /** Nº total */
  numTotal: number;
  /** Estado del acta */
  estadoActa: TipoEstadoActa;
  /** Número de evaluacines no evaluadas. */
  numEvaluacionesNoEvaluadas: number;
  /** referencia documentos */
  documentos: IActaDocumento[];
}
