import { IMemoria } from './memoria';
import { TipoDocumento } from './tipo-documento';

export class DocumentacionMemoria {
  /** Id */
  id: number;

  /** Memoria */
  memoria: IMemoria;

  /** TIpo de documento */
  tipoDocumento: TipoDocumento;

  /** Ref del documento */
  documentoRef: string;

  /** Aportado */
  aportado: boolean;

  constructor(documentacionMemoria?: DocumentacionMemoria) {
    this.id = documentacionMemoria?.id;
    this.memoria = documentacionMemoria?.memoria;
    this.tipoDocumento = documentacionMemoria?.tipoDocumento;
    this.documentoRef = documentacionMemoria?.documentoRef;
    this.aportado = documentacionMemoria?.aportado;
  }
}
