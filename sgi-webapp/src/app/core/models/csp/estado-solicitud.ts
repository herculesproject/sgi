
import { ISolicitud } from './solicitud';

export enum TipoEstadoSolicitud {
  BORRADOR = 'Borrador',
  PRESENTADA = 'Presentada',
  ADMITIDA_PROVISIONAL = 'Admitida provisional',
  EXCLUIDA_PROVISIONAL = 'Excluida provisional',
  ALEGADA_ADMISION = 'Alegada admisión',
  EXCLUIDA = 'Excluida',
  ADMITIDA_DEFINITIVA = 'Admitida definitiva',
  CONCECIDA_PROVISIONAL = 'Concedida provisional',
  DENEGADA_PROVISIONAL = 'Denegada provisional',
  ALEGADA_CONCESION = 'Alegada concesión',
  DESISTIDA = 'Desistida',
  CONCECIDA = 'Concedida',
  DENEGADA = 'Denegada',
}

export interface IEstadoSolicitud {

  /** Id */
  id: number;

  /** Solicitud */
  solicitud: ISolicitud;

  /** Estado */
  estado: TipoEstadoSolicitud;

  /** Fecha estado */
  fechaEstado: Date;

  /** Comentario */
  comentario: string;


}
