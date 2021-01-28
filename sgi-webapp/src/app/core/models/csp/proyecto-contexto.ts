import { IAreaTematica } from "./area-tematica";
import { IProyecto } from "./proyecto";

export enum PropiedadResultados {
  SIN_RESULTADOS = 'Sin resultados',
  UNIVERSIDAD = 'Universidad',
  ENTIDAD_FINANCIADORA = 'Entidad financiadora',
  COMPARTIDA = 'Compartida'
}


export interface IProyectoContexto {
  id: number;
  proyecto: IProyecto;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
  propiedadResultados: string;
  areaTematica: IAreaTematica;
  areaTematicaConvocatoria: IAreaTematica;
}