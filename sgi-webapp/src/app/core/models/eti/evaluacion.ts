import { Comite } from './comite';
import { ConvocatoriaReunion } from './convocatoria-reunion';
import { Dictamen } from './dictamen';
import { Memoria } from './memoria';
import { TipoEvaluacion } from './tipo-evaluacion';

export class Evaluacion {
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

  constructor(evaluacion?: Evaluacion) {
    this.id = evaluacion?.id;
    this.memoria = evaluacion?.memoria;
    this.comite = evaluacion?.comite;
    this.convocatoriaReunion = evaluacion?.convocatoriaReunion;
    this.tipoEvaluacion = evaluacion?.tipoEvaluacion;
    this.version = evaluacion?.version;
    this.dictamen = evaluacion?.dictamen;
    this.fechaDictamen = evaluacion?.fechaDictamen;
    this.esRevMinima = evaluacion?.esRevMinima;
    this.activo = evaluacion?.activo;
  }
}
