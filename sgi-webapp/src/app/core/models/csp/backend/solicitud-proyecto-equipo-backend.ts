import { IRolProyecto } from '../rol-proyecto';
import { ISolicitudProyectoDatosBackend } from './solicitud-proyecto-datos-backend';

export interface ISolicitudProyectoEquipoBackend {
  id: number;
  solicitudProyectoDatos: ISolicitudProyectoDatosBackend;
  personaRef: string;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}
