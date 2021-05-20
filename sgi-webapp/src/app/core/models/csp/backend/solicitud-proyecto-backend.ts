import { IAreaTematica } from '../area-tematica';
import { TipoPresupuesto } from '../solicitud-proyecto';

export interface ISolicitudProyectoBackend {
  id: number;
  titulo: string;
  acronimo: string;
  codExterno: string;
  duracion: number;
  colaborativo: boolean;
  coordinadorExterno: boolean;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
  areaTematica: IAreaTematica;
  checkListRef: string;
  envioEtica: boolean;
  tipoPresupuesto: TipoPresupuesto;
  importeSolicitado: number;
  importePresupuestado: number;
  importeSolicitadoSocios: number;
  importePresupuestadoSocios: number;
  totalImporteSolicitado: number;
  totalImportePresupuestado: number;
}
