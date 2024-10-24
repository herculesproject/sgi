import { IModeloEjecucion, ITipoDocumento } from "@core/models/csp/tipos-configuracion";
import { IModeloTipoFaseResponse } from "../modelo-tipo-fase/modelo-tipo-fase-response";

export interface IModeloTipoDocumentoResponse {
  id: number;
  tipoDocumento: ITipoDocumento;
  modeloEjecucion: IModeloEjecucion;
  modeloTipoFase: IModeloTipoFaseResponse;
  activo: boolean;
}
