import { Comite } from './comite';
import { ConvocatoriaReunion } from './convocatoria-reunion';
import { Dictamen } from './dictamen';
import { Memoria } from './memoria';
import { TipoEvaluacion } from './tipo-evaluacion';

export class IEvaluacion {
  /** ID */
  id: number;

  /** Memoria */
  memoria: Memoria;

  /** Comite */
  comite: Comite;

  /** Convocatoria reunión */
  convocatoriaReunion: ConvocatoriaReunion;

  /** Tipo evaluación */
  tipoEvaluacion: TipoEvaluacion;

  /** Version */
  version: number;

  /** Dictamen */
  dictamen: Dictamen;

  /** Fecha Inicio. */
  fechaDictamen: Date;

  /** Es revisión mínima */
  esRevMinima: boolean;

  /** Activo */
  activo: boolean;
}
