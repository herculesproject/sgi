
import { Dictamen } from './dictamen';

export class MemoriaListado {
  /** Id */
  id: number;

  /** número de referencia */
  numReferencia: string;

  /** Version */
  version: number;

  dictamen: Dictamen;

  constructor(id: number, numReferencia: string, version: number, dictamen: Dictamen) {
    this.id = id;
    this.numReferencia = numReferencia;
    this.version = version;
    this.dictamen = dictamen;
  }

}
