import { IRolSocio } from '../rol-socio';
import { ISolicitudProyectoDatosBackend } from './solicitud-proyecto-datos-backend';

export interface ISolicitudProyectoSocioBackend {
  id: number;
  solicitudProyectoDatos: ISolicitudProyectoDatosBackend;
  empresaRef: string;
  rolSocio: IRolSocio;
  mesInicio: number;
  mesFin: number;
  numInvestigadores: number;
  importeSolicitado: number;
}
