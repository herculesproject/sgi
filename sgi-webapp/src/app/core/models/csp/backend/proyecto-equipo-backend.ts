import { IRolProyecto } from '../rol-proyecto';
import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoEquipoBackend {
  id: number;
  proyecto: IProyectoBackend;
  rolProyecto: IRolProyecto;
  personaRef: string;
  horasDedicacion: number;
  fechaInicio: string;
  fechaFin: string;
}
