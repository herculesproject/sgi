
import { IRolProyecto } from '../rol-proyecto';
import { IProyectoSocioBackend } from './proyecto-socio-backend';

export interface IProyectoSocioEquipoBackend {
  id: number;
  proyectoSocio: IProyectoSocioBackend;
  rolProyecto: IRolProyecto;
  personaRef: string;
  fechaInicio: string;
  fechaFin: string;
}
