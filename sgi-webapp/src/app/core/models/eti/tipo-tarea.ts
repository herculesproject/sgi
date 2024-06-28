import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TIPO_TAREA {
  DISENIO_PROYECTO_PROCEDIMIENTOS = 1,
  EUTANASIA = 2,
  MANIPULACION_ANIMALES = 3
}

export const TIPO_TAREA_MAP: Map<TIPO_TAREA, string> = new Map([
  [TIPO_TAREA.DISENIO_PROYECTO_PROCEDIMIENTOS, marker(`eti.tipo-tarea.DISENIO_PROYECTO_PROCEDIMIENTOS`)],
  [TIPO_TAREA.EUTANASIA, marker(`eti.tipo-tarea.EUTANASIA`)],
  [TIPO_TAREA.MANIPULACION_ANIMALES, marker(`eti.tipo-tarea.MANIPULACION_ANIMALES`)]
]);

export class TipoTarea {

  /** ID */
  id: number;

  /** Nombre */
  nombre: string;

  /** Activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.nombre = null;
    this.activo = true;
  }

}
