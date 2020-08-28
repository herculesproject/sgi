
import { PeticionEvaluacion } from './peticion-evaluacion';
import { Comite } from './comite';
import { TipoMemoria } from './tipo-memoria';
import { TipoEstadoMemoria } from './tipo-estado-memoria';

export class Memoria {
  /** Id */
  id: number;

  numReferencia: string;

  /** Petición evaluación */
  peticionEvaluacion: PeticionEvaluacion;

  /** Comité */
  comite: Comite;

  /** Título */
  titulo: string;

  /** Referencia persona */
  personaRef: string;

  /** Tipo Memoria */
  tipoMemoria: TipoMemoria;

  /** Fecha envio secretaria. */
  fechaEnvioSecretaria: Date;

  /** Indicador require retrospectiva */
  requiereRetrospectiva: boolean;

  /** Fecha retrospectiva. */
  fechaRetrospectiva: Date;

  /** Version */
  version: number;

  /** Estado Memoria Actual */
  estadoActual: TipoEstadoMemoria;


}
