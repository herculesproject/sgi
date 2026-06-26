import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { TipoEntidad } from '@core/models/rel/relacion';

export interface IRelacionEntidadResponse {
  id: number;
  titulo: I18nFieldValueResponse[];
  codigoExterno?: string;
  codigosSge?: string;
}

export interface IProyectoRelacionResponse {
  id: number;
  tipoEntidadRelacionada: TipoEntidad;
  entidadRelacionada: IRelacionEntidadResponse;
  observaciones: I18nFieldValueResponse[];
}
