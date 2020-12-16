import { IProyecto } from './proyecto';

export enum TipoEstadoProyecto {
  BORRADOR = 'Borrador',
  PROVISIONAL = 'Provisional',
  ABIERTO = 'Abierto',
  FINALIZADO = 'Finalizado',
  CANCELADO = 'Cancelado'
}

export interface IEstadoProyecto {

  /** Id */
  id: number;

  /** Solicitud */
  solicitud: IProyecto;

  /** Estado */
  estado: TipoEstadoProyecto;

  /** Fecha estado */
  fechaEstado: Date;

  /** Comentario */
  comentario: string;


}
