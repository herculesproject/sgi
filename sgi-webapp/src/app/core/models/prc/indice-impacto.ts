import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProduccionCientifica } from './produccion-cientifica';

export interface IIndiceImpacto {
  id: number;
  ranking: TipoRanking;
  anio: number;
  otraFuenteImpacto: string;
  indice: number;
  posicionPublicacion: number;
  numeroRevistas: number;
  revista25: boolean;
  produccionCientifica: IProduccionCientifica;
  tipoFuenteImpacto: string;
}

export enum TipoRanking {
  /** Clase1 */
  CLASE1 = 'CLASE1',
  /** Clase2 */
  CLASE2 = 'CLASE2',
  /** Clase3 */
  CLASE3 = 'CLASE3',
  /** A* */
  A_POR = 'A_POR',
  /** A */
  A = 'A'
}

export enum Cuartil {
  Q1 = 'Q1',
  Q2 = 'Q2',
  Q3 = 'Q3',
  Q4 = 'Q4'
}

export enum TipoFuenteImpacto {
  /** WOS */
  E000 = '000',
  /** JCR */
  E010 = 'JCR',
  /** INRECS */
  E020 = 'INRECS',
  /** BCI */
  BCI = 'BCI',
  /** ICEE */
  ICEE = 'ICEE',
  /** DIALNET */
  DIALNET = 'DIALNET',
  /** CitEC */
  CITEC = 'CITEC',
  /** SCIMAGO */
  SCIMAGO = 'SCIMAGO',
  /** ERIH */
  ERIH = 'ERIH',
  /** MIAR */
  MIAR = 'MIAR',
  /** FECYT */
  FECYT = 'FECYT',
  /** GII-GRIN-SCIE */
  GII_GRIN_SCIE = 'GII-GRIN-SCIE',
  /** CORE */
  CORE = 'CORE',
  /** Otros */
  OTHERS = 'OTHERS'
}

export const MSG_TIPO_FUENTE_IMPACTO_OTHERS = marker('prc.indice-impacto.tipo-fuente.OTHERS');
