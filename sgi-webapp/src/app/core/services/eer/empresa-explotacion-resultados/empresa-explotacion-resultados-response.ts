import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";
import { EstadoEmpresa, TipoEmpresa } from "@core/models/eer/empresa-explotacion-resultados";

export interface IEmpresaExplotacionResultadosResponse {
  id: number;
  fechaSolicitud: string;
  tipoEmpresa: TipoEmpresa;
  solicitanteRef: string;
  nombreRazonSocial: I18nFieldValueResponse[];
  entidadRef: string;
  objetoSocial: I18nFieldValueResponse[];
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
  activo: boolean;
}
