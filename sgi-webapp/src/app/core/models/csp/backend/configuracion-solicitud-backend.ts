import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IConvocatoria } from '../convocatoria';
import { IConvocatoriaFaseBackend } from './convocatoria-fase-backend';

export interface IConfiguracionSolicitudBackend {
  /** Id */
  id: number;
  /** Convocatoria */
  convocatoria: IConvocatoria;
  /** Tramitacion SGI */
  tramitacionSGI: boolean;
  /** Convocatoria Fase */
  fasePresentacionSolicitudes: IConvocatoriaFaseBackend;
  /** Importe Máximo Solicitud */
  importeMaximoSolicitud: number;
  /** Tipo Formulario Solicitud */
  formularioSolicitud: FormularioSolicitud;
}
