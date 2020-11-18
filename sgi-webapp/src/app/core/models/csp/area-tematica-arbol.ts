export interface IAreaTematicaArbol {
  id: number;
  abreviatura: string;
  nombre: string;
  padre: IAreaTematicaArbol;
  activo: boolean;
}

