import { IEvaluacion } from './evaluacion';
import { Persona } from '../sgp/persona';
import { Memoria } from './memoria';
import { Comite } from './comite';
import { ConvocatoriaReunion } from './convocatoria-reunion';
import { TipoEvaluacion } from './tipo-evaluacion';
import { Dictamen } from './dictamen';

export class EvaluacionConSolicitante implements IEvaluacion {
  id: number;
  memoria: Memoria;
  comite: Comite;
  convocatoriaReunion: ConvocatoriaReunion;
  tipoEvaluacion: TipoEvaluacion;
  version: number;
  dictamen: Dictamen;
  fechaDictamen: Date;
  esRevMinima: boolean;
  activo: boolean;
  persona: Persona;

}
