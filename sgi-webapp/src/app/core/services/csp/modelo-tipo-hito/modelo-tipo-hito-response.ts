import { IModeloEjecucion } from "@core/models/csp/tipos-configuracion";
import { ITipoHitoResponse } from "../tipo-hito/tipo-hito-response";

export interface IModeloTipoHitoResponse {
  id: number;
  tipoHito: ITipoHitoResponse;
  modeloEjecucion: IModeloEjecucion;
  convocatoria: boolean;
  proyecto: boolean;
  solicitud: boolean;
  activo: boolean;
}
