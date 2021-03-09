import { IConceptoGasto } from '../tipos-configuracion';
import { ISolicitudProyectoDatosBackend } from './solicitud-proyecto-datos-backend';

export interface ISolicitudProyectoPresupuestoBackend {
  id: number;
  solicitudProyectoDatos: ISolicitudProyectoDatosBackend;
  conceptoGasto: IConceptoGasto;
  entidadRef: string;
  anualidad: number;
  importeSolicitado: number;
  observaciones: string;
  financiacionAjena: boolean;
}
