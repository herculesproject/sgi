import { ITipoFaseResponse } from '@core/services/csp/tipo-fase/tipo-fase-response';
import { ITipoDocumentoResponse } from '../tipo-documento/tipo-documento-response';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';

export interface IProyectoDocumentoResponse {
  id: number;
  proyectoId: number;
  nombre: I18nFieldValueResponse[];
  documentoRef: string;
  tipoFase: ITipoFaseResponse;
  tipoDocumento: ITipoDocumentoResponse;
  comentario: string;
  visible: boolean;
}
