
import { IModeloTipoFase } from './modelo-tipo-fase';
import { IModeloEjecucion, ITipoDocumento } from './tipos-configuracion';

export interface IModeloTipoDocumento {
  id: number;
  tipoDocumento: ITipoDocumento;
  modeloEjecucion: IModeloEjecucion;
  tipoFase: IModeloTipoFase;
  activo: boolean;
}
