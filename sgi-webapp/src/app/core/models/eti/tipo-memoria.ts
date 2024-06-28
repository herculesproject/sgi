import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum TIPO_MEMORIA {
  NUEVA = 1,
  MODIFICACION = 2,
  RATIFICACION = 3
}

export const TIPO_MEMORIA_MAP: Map<TIPO_MEMORIA, string> = new Map([
  [TIPO_MEMORIA.NUEVA, marker(`eti.tipo-memoria.NUEVA`)],
  [TIPO_MEMORIA.MODIFICACION, marker(`eti.tipo-memoria.MODIFICACION`)],
  [TIPO_MEMORIA.RATIFICACION, marker(`eti.tipo-memoria.RATIFICACION`)]
]);

export interface ITipoMemoria {
  /** ID */
  id: number;
  /** Nombre */
  nombre: string;
  /** Activo */
  activo: boolean;
}
