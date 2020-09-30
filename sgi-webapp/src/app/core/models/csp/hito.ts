import { ITipoDocumento } from '../eti/tipo-documento';
import { ITipoHito } from './tipo-hito';

export interface IHito {
  /** Id */
  id: number;

  /** Fecha inicio  */
  fechaInicio: Date;

  /** Tipo de hito */
  tipoHito: ITipoHito;

  /** Comentario */
  comentario: string;

  /** Aviso */
  aviso: boolean;

}
