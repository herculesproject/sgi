import { IModeloEjecucion } from "@core/models/csp/tipos-configuracion";
import { ITipoFinalidadResponse } from "../tipo-finalidad/tipo-finalidad-response";

export interface IModeloTipoFinalidadResponse {
  id: number;
  tipoFinalidad: ITipoFinalidadResponse;
  modeloEjecucion: IModeloEjecucion;
  activo: boolean;
}
