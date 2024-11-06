import { IModeloEjecucion, ITipoDocumento } from "@core/models/csp/tipos-configuracion";
import { IModeloTipoFaseResponse } from "../modelo-tipo-fase/modelo-tipo-fase-response";
import { ITipoDocumentoResponse } from "../tipo-documento/tipo-documento-response";

export interface IModeloTipoDocumentoResponse {
  id: number;
  tipoDocumento: ITipoDocumentoResponse;
  modeloEjecucion: IModeloEjecucion;
  modeloTipoFase: IModeloTipoFaseResponse;
  activo: boolean;
}
