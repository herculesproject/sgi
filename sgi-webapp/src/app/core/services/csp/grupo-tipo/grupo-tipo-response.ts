import { ITipoGrupoResponse } from '../tipo-grupo/tipo-grupo-response';

export interface IGrupoTipoResponse {
  id: number;
  tipoGrupo: ITipoGrupoResponse;
  grupoId: number;
  fechaInicio: string;
  fechaFin: string;
}
