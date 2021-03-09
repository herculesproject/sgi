import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoEntidadGestoraBackend {
  id: number;
  proyecto: IProyectoBackend;
  entidadRef: string;
}
