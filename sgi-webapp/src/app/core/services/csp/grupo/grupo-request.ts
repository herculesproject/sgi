import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';

export interface IGrupoRequest {
  acronimo: string;
  nombre: I18nFieldValueRequest[];
  fechaInicio: string;
  fechaFin: string;
  direccion: string;
  email: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipoGrupoId: number;
  especialInvestigacion: boolean;
  resumen: I18nFieldValueRequest[];
  departamentoOrigenRef: string;
}
