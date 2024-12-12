import { IAreaTematicaResponse } from '@core/services/csp/area-tematica/area-tematica-response';
import { PropiedadResultados } from '../proyecto-contexto';

export interface IProyectoContextoBackend {
  id: number;
  proyectoId: number;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
  propiedadResultados: PropiedadResultados;
  areaTematica: IAreaTematicaResponse;
}
