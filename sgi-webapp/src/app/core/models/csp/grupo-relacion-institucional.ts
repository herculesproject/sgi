import { IEmpresa } from '@core/models/sgemp/empresa';
import { IGrupo } from './grupo';

export interface IGrupoRelacionInstitucional {
  id: number;
  grupo: IGrupo;
  entidad: IEmpresa;
  institucion: string;
}
