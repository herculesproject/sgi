import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export interface IConvocatoriaPartidaPresupuestaria {
  id: number;
  convocatoriaId: number;
  codigo: string;
  descripcion: string;
  tipoPartida: TipoPartida;
}


export enum TipoPartida {
  GASTO = 'GASTO',
  INGRESO = 'INGRESO'
}

export const TIPO_PARTIDA_MAP: Map<TipoPartida, string> = new Map([
  [TipoPartida.GASTO, marker(`csp.convocatoria-partida-presupuestaria.tipo-partida.GASTO`)],
  [TipoPartida.INGRESO, marker(`csp.convocatoria-partida-presupuestaria.tipo-partida.INGRESO`)]
]);