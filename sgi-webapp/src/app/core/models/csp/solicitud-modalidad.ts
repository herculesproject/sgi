import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { IPrograma } from './programa';

export interface ISolicitudModalidad {
  /** Id */
  id: number;
  /** Id de Solicitud */
  solicitudId: number;
  /** Entidad */
  entidad: IEmpresaEconomica;
  /** Programa */
  programa: IPrograma;
}
