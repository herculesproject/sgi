import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';

export interface IProyectoPeriodoSeguimientoDocumentoResponse {
  id: number;
  proyectoPeriodoSeguimientoId: number;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumentoResponse;
  visible: boolean;
  comentario: string;
}
