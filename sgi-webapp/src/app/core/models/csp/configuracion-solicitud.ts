import { TipoBaremacionEnum } from '@core/enums/tipo-baremacion';
import { TipoFormularioSolicitudEnum } from '@core/enums/tipo-formulario-solicitud';
import { IConvocatoria } from './convocatoria';
import { IConvocatoriaFase } from './convocatoria-fase';

export interface IConfiguracionSolicitud {
  /** Id */
  id: number;

  /** Convocatoria */
  convocatoria: IConvocatoria;

  /** Tramitacion SGI */
  tramitacionSGI: boolean;

  /** Convocatoria Fase */
  fasePresentacionSolicitudes: IConvocatoriaFase;

  /** Importe Máximo Solicitud */
  importeMaximoSolicitud: number;

  /** Tipo Formulario Solicitud */
  formularioSolicitud: TipoFormularioSolicitudEnum;

  /** Baremación */
  baremacionRef: TipoBaremacionEnum;
}
