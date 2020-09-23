import { Persona } from '../sgp/persona';
import { IEvaluacion } from './evaluacion';
import { IMemoria } from './memoria';
import { Comite } from './comite';
import { ConvocatoriaReunion } from './convocatoria-reunion';
import { TipoEvaluacion } from './tipo-evaluacion';
import { IDictamen } from './dictamen';
import { IEvaluador } from './evaluador';

export class EvaluacionConSolicitante implements IEvaluacion {
  id: number;
  memoria: IMemoria;
  comite: Comite;
  convocatoriaReunion: ConvocatoriaReunion;
  tipoEvaluacion: TipoEvaluacion;
  version: number;
  dictamen: IDictamen;
  fechaDictamen: Date;
  esRevMinima: boolean;
  evaluador1: IEvaluador;
  evaluador2: IEvaluador;
  activo: boolean;

  /** Persona */
  persona: Persona;
}
