import { IFuenteFinanciacion } from '../fuente-financiacion';
import { ITipoFinanciacion } from '../tipos-configuracion';
import { ISolicitudProyectoDatosBackend } from './solicitud-proyecto-datos-backend';

export interface ISolicitudProyectoEntidadFinanciadoraAjenaBackend {
  id: number;
  entidadRef: string;
  solicitudProyectoDatos: ISolicitudProyectoDatosBackend;
  fuenteFinanciacion: IFuenteFinanciacion;
  tipoFinanciacion: ITipoFinanciacion;
  porcentajeFinanciacion: number;
}
