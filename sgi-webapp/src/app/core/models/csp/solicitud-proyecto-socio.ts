import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { IRolSocio } from './rol-socio';

export interface ISolicitudProyectoSocio {
  id: number;
  solicitudProyectoId: number;
  empresa: IEmpresaEconomica;
  rolSocio: IRolSocio;
  mesInicio: number;
  mesFin: number;
  numInvestigadores: number;
  importeSolicitado: number;
}
