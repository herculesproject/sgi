import { Language } from '@core/services/language.service';
import { IActa } from './acta';
import { IMemoria } from './memoria';
import { TipoEvaluacion } from './tipo-evaluacion';

export interface IInformeDocumento {
  /** Referencia documento */
  documentoRef: string;
  /** id informe */
  informeId: number;
  /** Language */
  lang: string;
}
