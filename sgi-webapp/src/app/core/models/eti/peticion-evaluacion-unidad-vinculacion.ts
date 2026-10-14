import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';

export interface IPeticionEvaluacionUnidadVinculacion {
  id: number;
  peticionEvaluacionId: number;
  unidadVinculacion: IUnidadVinculacion;
}
