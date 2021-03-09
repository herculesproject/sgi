import { IRolProyecto } from '../rol-proyecto';
import { ISolicitudProyectoSocioBackend } from './solicitud-proyecto-socio-backend';

export interface ISolicitudProyectoEquipoSocioBackend {
  id: number;
  solicitudProyectoSocio: ISolicitudProyectoSocioBackend;
  personaRef: string;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}
