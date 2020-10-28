import { IListadoAreaTematica } from './listado-area-tematica';

export interface IAreaTematicaArbol {
  id: number;
  abreviatura: string;
  nombre: string;
  listadoAreaTematica: IListadoAreaTematica;
  padre: IAreaTematicaArbol;
  activo: boolean;
}

