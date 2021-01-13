export enum TipoEquipoProyectoEnum {
  INVESTIGACION = 'Equipo de investigación',
  TRABAJO = 'Equipo de trabajo'
}

export interface IRolProyecto {
  id: number;
  abreviatura: string;
  nombre: string;
  descripcion: string;
  rolPrincipal: boolean;
  responsableEconomico: boolean;
  equipo: TipoEquipoProyectoEnum;
  colectivoRef: string;
  activo: boolean;
}
