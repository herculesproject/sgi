import { ITipoDocumento } from '../tipos-configuracion';
import { IProyectoSocioPeriodoJustificacionBackend } from './proyecto-socio-periodo-justificacion-backend';

export interface ISocioPeriodoJustificacionDocumentoBackend {
  id: number;
  proyectoSocioPeriodoJustificacion: IProyectoSocioPeriodoJustificacionBackend;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  comentario: string;
  visible: boolean;
}
