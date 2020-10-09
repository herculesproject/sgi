import { ITipoHito } from './tipos-configuracion';

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
