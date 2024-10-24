import { IModeloEjecucion } from "@core/models/csp/tipos-configuracion";
import { ITipoFaseResponse } from "../tipo-fase/tipo-fase-response";

export interface IModeloTipoFaseResponse {
  id: number;
  tipoFase: ITipoFaseResponse;
  modeloEjecucion: IModeloEjecucion;
  convocatoria: boolean;
  proyecto: boolean;
  solicitud: boolean;
  activo: boolean;
}
