import { IRolSocio } from '../rol-socio';
import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoSocioBackend {
  id: number;
  proyecto: IProyectoBackend;
  empresaRef: string;
  rolSocio: IRolSocio;
  fechaInicio: string;
  fechaFin: string;
  numInvestigadores: number;
  importeConcedido: number;
}
