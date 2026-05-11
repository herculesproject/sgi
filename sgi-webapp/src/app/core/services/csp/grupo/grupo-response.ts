import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { ITipoGrupoResponse } from '../tipo-grupo/tipo-grupo-response';

export interface IGrupoResponse {
  id: number;
  acronimo: string;
  nombre: I18nFieldValueResponse[];
  direccion: string;
  email: string;
  fechaInicio: string;
  fechaFin: string;
  imagenRef: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipoGrupo: ITipoGrupoResponse;
  especialInvestigacion: boolean;
  resumen: I18nFieldValueResponse[];
  activo: boolean;
}
