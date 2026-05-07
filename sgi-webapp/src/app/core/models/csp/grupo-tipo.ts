import { DateTime } from 'luxon';
import { IGrupo } from './grupo';
import { ITipoGrupo } from './tipo-grupo';

export interface IGrupoTipo {
  id: number;
  tipoGrupo: ITipoGrupo;
  fechaInicio: DateTime;
  fechaFin: DateTime;
  grupo: IGrupo;
}
