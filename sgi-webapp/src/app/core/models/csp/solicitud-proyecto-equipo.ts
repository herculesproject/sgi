import { IPersona } from '../sgp/persona';
import { IRolProyecto } from './rol-proyecto';

export interface ISolicitudProyectoEquipo {
  id: number;
  solicitudProyectoId: number;
  persona: IPersona;
  rolProyecto: IRolProyecto;
  mesInicio: number;
  mesFin: number;
}
