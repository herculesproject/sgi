import { IModeloEjecucion } from './tipos-configuracion';

export interface IModeloUnidad {
  id: number;
  unidadGestionRef: string;
  modeloEjecucion: IModeloEjecucion;
  activo: boolean;
}