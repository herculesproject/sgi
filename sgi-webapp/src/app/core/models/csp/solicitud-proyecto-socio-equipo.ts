import { IPersona } from '../sgp/persona';
import { IRolProyecto } from './rol-proyecto';

export interface ISolicitudProyectoSocioEquipo {
  id: number;
  solicitudProyectoSocioId: number;
  persona: IPersona;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}
