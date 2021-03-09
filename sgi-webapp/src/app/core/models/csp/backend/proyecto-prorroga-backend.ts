import { Tipo } from '../proyecto-prorroga';
import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoProrrogaBackend {
  id: number;
  proyecto: IProyectoBackend;
  numProrroga: number;
  fechaConcesion: string;
  tipo: Tipo;
  fechaFin: string;
  importe: number;
  observaciones: string;
}
