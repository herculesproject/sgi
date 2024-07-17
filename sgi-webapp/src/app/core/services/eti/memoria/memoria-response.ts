import { IComite } from '../../../models/eti/comite';
import { TipoEstadoMemoria } from '../../../models/eti/tipo-estado-memoria';
import { ITipoMemoria } from '../../../models/eti/tipo-memoria';
import { IPeticionEvaluacionResponse } from '../peticion-evaluacion/peticion-evaluacion-response';
import { IRetrospectivaBackend } from '../../../models/eti/backend/retrospectiva-backend';

export interface IMemoriaResponse {
  /** Id */
  id: number;

  numReferencia: string;
  /** Petición evaluación */
  peticionEvaluacion: IPeticionEvaluacionResponse;
  /** Comité */
  comite: IComite;
  /** Título */
  titulo: string;
  /** Referencia persona responsable */
  personaRef: string;
  /** Tipo Memoria */
  tipoMemoria: ITipoMemoria;
  /** Fecha envio secretaria. */
  fechaEnvioSecretaria: string;
  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;
  /** Version */
  version: number;
  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;
  /** Responsable de memoria */
  isResponsable: boolean;
  /** Retrospectiva */
  retrospectiva: IRetrospectivaBackend;
  /** Memoria original */
  memoriaOriginal: IMemoriaResponse;
  /** Activo */
  activo: boolean;
}
