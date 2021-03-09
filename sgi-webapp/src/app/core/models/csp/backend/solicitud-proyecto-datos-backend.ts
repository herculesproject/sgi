import { IAreaTematica } from '../area-tematica';
import { ISolicitudBackend } from './solicitud-backend';

export interface ISolicitudProyectoDatosBackend {
  id: number;
  solicitud: ISolicitudBackend;
  titulo: string;
  acronimo: string;
  duracion: number;
  colaborativo: boolean;
  coordinadorExterno: boolean;
  universidadSubcontratada: boolean;
  objetivos: string;
  intereses: string;
  resultadosPrevistos: string;
  areaTematica: IAreaTematica;
  checkListRef: string;
  envioEtica: boolean;
  presupuestoPorEntidades: boolean;
}
