import { TipoPropiedad } from "@core/enums/tipo-propiedad";
import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IViaProteccionRequest {

  nombre: I18nFieldValueRequest[];
  descripcion: string;
  tipoPropiedad: TipoPropiedad;
  mesesPrioridad: number;
  paisEspecifico: boolean;
  extensionInternacional: boolean;
  variosPaises: boolean;
}
