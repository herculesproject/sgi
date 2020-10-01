import { IPersona } from '../sgp/persona';
import { CargoComite } from './cargo-comite';
import { Comite } from './comite';
import { IEvaluador } from './evaluador';

export interface IConflictoInteres extends IPersona {

  /** Id */
  id: number;

  /** Evaluador */
  evaluador: IEvaluador;

  /** Referencia persona conflicto */
  personaConflictoRef: string;

}
