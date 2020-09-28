import { IPersona } from '../sgp/persona';
import { CargoComite } from './cargo-comite';
import { Comite } from './comite';

export interface IEvaluador extends IPersona {

  /** Id */
  id: number;

  /** Comité */
  comite: Comite;

  /** Cargo comité */
  cargoComite: CargoComite;

  /** Resumen */
  resumen: string;

  /** Fecha Alta. */
  fechaAlta: Date;

  /** Fecha Baja. */
  fechaBaja: Date;

  /** Referencia persona */
  personaRef: string;

  /** Activo */
  activo: boolean;

}
