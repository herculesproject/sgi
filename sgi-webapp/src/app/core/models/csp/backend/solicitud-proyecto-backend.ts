import { IAreaTematicaResponse } from '@core/services/csp/area-tematica/area-tematica-response';
import { TipoPresupuesto } from '../solicitud-proyecto';

export interface ISolicitudProyectoBackend {
  id: number;
  acronimo: string;
  codExterno: string;
  duracion: number;
  colaborativo: boolean;
  coordinado: boolean;
  rolUniversidadId: number;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
  areaTematica: IAreaTematicaResponse;
  checklistRef: string;
  peticionEvaluacionRef: string;
  tipoPresupuesto: TipoPresupuesto;
  importeSolicitado: number;
  importePresupuestado: number;
  importePresupuestadoCostesIndirectos: number;
  importeSolicitadoCostesIndirectos: number;
  importeSolicitadoSocios: number;
  importePresupuestadoSocios: number;
  totalImporteSolicitado: number;
  totalImportePresupuestado: number;
}
