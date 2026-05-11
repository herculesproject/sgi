import { I18nFieldValueRequest } from '@core/i18n/i18n-field-request';

export interface IGrupoRequest {
  acronimo: string;
  nombre: I18nFieldValueRequest[];
  direccion: string;
  email: string;
  fechaInicio: string;
  fechaFin: string;
  imagenRef: string;
  proyectoSgeRef: string;
  solicitudId: number;
  codigo: string;
  tipoGrupoId: number;
  especialInvestigacion: boolean;
  resumen: I18nFieldValueRequest[];
  departamentoOrigenRef: string;
}
