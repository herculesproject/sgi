
import { Comite } from './comite';
import { CargoComite } from './cargo-comite';
import { UsuarioInfo } from '../sgp/usuario-info';

export interface Evaluador extends UsuarioInfo {

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
