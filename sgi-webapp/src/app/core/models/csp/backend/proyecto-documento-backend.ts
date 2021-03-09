import { ITipoDocumento, ITipoFase } from '../tipos-configuracion';
import { IProyectoBackend } from './proyecto-backend';

export interface IProyectoDocumentoBackend {
  id: number;
  proyecto: IProyectoBackend;
  nombre: string;
  documentoRef: string;
  tipoFase: ITipoFase;
  tipoDocumento: ITipoDocumento;
  comentario: string;
  visible: boolean;
}
