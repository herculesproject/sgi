import { ITipoDocumento } from '../tipos-configuracion';
import { IProyectoPeriodoSeguimientoBackend } from './proyecto-periodo-seguimiento-backend';

export interface IProyectoPeriodoSeguimientoDocumentoBackend {
  id: number;
  proyectoPeriodoSeguimiento: IProyectoPeriodoSeguimientoBackend;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  visible: boolean;
  comentario: string;
}
