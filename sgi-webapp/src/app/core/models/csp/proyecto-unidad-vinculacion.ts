import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';

export interface IProyectoUnidadVinculacion {
  id: number;
  proyectoId: number;
  unidadVinculacion: IUnidadVinculacion;
}
