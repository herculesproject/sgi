import { IGrupo } from './grupo';
import { ITipoEnlace } from './tipos-configuracion';

export interface IGrupoEnlace {
  id: number;
  grupo: IGrupo;
  enlace: string;
  tipoEnlace: ITipoEnlace;
}
