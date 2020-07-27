import { ConvocatoriaReunion } from './convocatoria-reunion';
import { TipoEstadoActa } from './tipo-estado-acta';
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

  /** Referencia usuario */
  usuarioRef: string;

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

  constructor() {
    this.id = null;
    this.numReferencia = null;
    this.peticionEvaluacion = null;
    this.comite = null;
    this.titulo = null;
    this.usuarioRef = null;
    this.tipoMemoria = null;
    this.fechaEnvioSecretaria = null;
    this.requiereRetrospectiva = null;
    this.fechaRetrospectiva = null;
    this.version = null;
    this.estadoActual = null;
  }
}
