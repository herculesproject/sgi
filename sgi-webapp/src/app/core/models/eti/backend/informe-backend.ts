import { IInformeDocumento } from '../informe-documento';
import { TipoEvaluacion } from '../tipo-evaluacion';
import { IActaBackend } from './acta-backend';
import { IMemoriaBackend } from './memoria-backend';

export interface IInformeBackend {
  /** Id */
  id: number;
  /** Memoria */
  memoria: IMemoriaBackend;
  /** referencia */
  informeDocumentos: IInformeDocumento[];
  /** Version */
  version: number;
  /** Tipo Evaluación */
  tipoEvaluacion: TipoEvaluacion;
}
