import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';

export interface ISolicitudProyectoUnidadVinculacion {
  id: number;
  solicitudProyectoId: number;
  unidadVinculacion: IUnidadVinculacion;
}
