import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { IPartidaGasto } from './partida-gasto';

export interface ISolicitudProyectoPresupuesto extends IPartidaGasto {
  solicitudProyectoId: number;
  empresa: IEmpresaEconomica;
  financiacionAjena: boolean;
}
