import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoGrupoResponse } from '../tipo-grupo/tipo-grupo-response';

export interface IGrupoResponse {
  id: number;
  nombre: I18nFieldValueResponse[];
  fechaInicio: string;
  fechaFin: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipoGrupo: ITipoGrupoResponse;
  especialInvestigacion: boolean;
  resumen: I18nFieldValueResponse[];
  activo: boolean;
}
