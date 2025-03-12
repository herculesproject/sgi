import { ITipoProcedimiento } from '@core/models/pii/tipo-procedimiento';
import { ITipoProcedimientoResponse } from '../../tipo-procedimiento/tipo-procedimiento-response';

export interface IProcedimientoResponse {

  id: number;
  fecha: string;
  tipoProcedimiento: ITipoProcedimientoResponse;
  solicitudProteccionId: number;
  accionATomar: string;
  fechaLimiteAccion: string;
  generarAviso: boolean;
  comentarios: string;

}
