import { ITipoDocumento } from '../tipos-configuracion';
import { IProyectoProrrogaBackend } from './proyecto-prorroga-backend';

export interface IProyectoProrrogaDocumentoBackend {
  id: number;
  proyectoProrroga: IProyectoProrrogaBackend;
  nombre: string;
  documentoRef: string;
  tipoDocumento: ITipoDocumento;
  visible: boolean;
  comentario: string;
}
