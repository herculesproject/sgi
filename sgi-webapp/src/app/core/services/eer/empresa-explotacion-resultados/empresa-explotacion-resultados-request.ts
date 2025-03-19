import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";
import { TipoEmpresa, EstadoEmpresa } from "@core/models/eer/empresa-explotacion-resultados";

export interface IEmpresaExplotacionResultadosRequest {
  fechaSolicitud: string;
  tipoEmpresa: TipoEmpresa;
  solicitanteRef: string;
  nombreRazonSocial: I18nFieldValueRequest[];
  entidadRef: string;
  objetoSocial: I18nFieldValueRequest[];
  conocimientoTecnologia: string;
  numeroProtocolo: string;
  notario: string;
  fechaConstitucion: string;
  fechaAprobacionCG: string;
  fechaIncorporacion: string;
  fechaDesvinculacion: string;
  fechaCese: string;
  observaciones: string;
  estado: EstadoEmpresa;
}
