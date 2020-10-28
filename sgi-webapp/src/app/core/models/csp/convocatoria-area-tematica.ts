import { IAreaTematicaArbol } from './area-tematica-arbol';
import { IConvocatoria } from './convocatoria';

export interface IConvocatoriaAreaTematica {
  id: number;
  areaTematicaArbol: IAreaTematicaArbol;
  convocatoria: IConvocatoria;
  observaciones: string;
}