import { IAreaTematica } from '../area-tematica';
import { PropiedadResultados } from '../proyecto-contexto';
import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoContextoBackend {
  id: number;
  proyecto: IProyectoBackend;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
  propiedadResultados: PropiedadResultados;
  areaTematica: IAreaTematica;
  areaTematicaConvocatoria: IAreaTematica;
}
