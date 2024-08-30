import { IComiteResponse } from '@core/services/eti/comite/comite-response';
import { CargoComite } from '../cargo-comite';

export interface IEvaluadorBackend {
  /** Id */
  id: number;
  /** Comité */
  comite: IComiteResponse;
  /** Cargo comité */
  cargoComite: CargoComite;
  /** Resumen */
  resumen: string;
  /** Fecha Alta. */
  fechaAlta: string;
  /** Fecha Baja. */
  fechaBaja: string;
  /** Referencia persona */
  personaRef: string;
  /** Activo */
  activo: boolean;
}
