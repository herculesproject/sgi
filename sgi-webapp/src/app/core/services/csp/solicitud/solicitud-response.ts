import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IEstadoSolicitudBackend } from '../../../models/csp/backend/estado-solicitud-backend';
import { OrigenSolicitud, TipoSolicitudGrupo } from '../../../models/csp/solicitud';

export interface ISolicitudResponse {
  /** Id */
  id: number;
  /** Título */
  titulo: I18nFieldValueResponse[];
  /** Activo */
  activo: boolean;
  /** Codigo externo */
  codigoExterno: string;
  /** Codigo registro interno */
  codigoRegistroInterno: string;
  /** Estado solicitud */
  estado: IEstadoSolicitudBackend;
  /** Id de Convocatoria */
  convocatoriaId: number;
  /** Convocatoria externa */
  convocatoriaExterna: string;
  /** Creador */
  creadorRef: string;
  /** Solicitante */
  solicitanteRef: string;
  /** Tipo formulario solicitud */
  formularioSolicitud: FormularioSolicitud;
  /** Tipo solicitud grupo */
  tipoSolicitudGrupo: TipoSolicitudGrupo;
  /** Unidad gestion */
  unidadGestionRef: string;
  /** Observaciones */
  observaciones: string;
  /** Año */
  anio: number;
  modeloEjecucionId: number;
  origenSolicitud: OrigenSolicitud;
  tipoFinalidadId: number;
}
