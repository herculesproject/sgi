import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';

export interface IGrupoUnidadVinculacion {
  id: number;
  grupoId: number;
  unidadVinculacion: IUnidadVinculacion;
}
