import { Persona } from '../sgp/persona';
import { CargoComite } from './cargo-comite';
import { Comite } from './comite';

export interface Evaluador extends Persona {

  /** Id */
  id: number;

  /** Comité */
  comite: Comite;

  /** Cargo Comité */
  cargoComite: CargoComite;

  /** Fecha Alta   */
  fechaAlta: Date;

  /** Fecha Baja */
  fechaBaja: Date;

  /** Resumen */
  resumen: string;

  /** Activo */
  activo: boolean;

}
