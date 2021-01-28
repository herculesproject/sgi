import { IEmpresaEconomica } from '../sgp/empresa-economica';
import { ISolicitudProyectoDatos } from './solicitud-proyecto-datos';
import { IConceptoGasto } from './tipos-configuracion';

export interface ISolicitudProyectoPresupuesto {
  id: number;
  solicitudProyectoDatos: ISolicitudProyectoDatos;
  conceptoGasto: IConceptoGasto;
  empresa: IEmpresaEconomica;
  anualidad: number;
  importeSolicitado: number;
  observaciones: string;
  financiacionAjena: boolean;
}
