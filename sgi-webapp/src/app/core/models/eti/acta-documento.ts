
export interface IActaDocumento {
  /** Referencia documento */
  documentoRef: string;
  /** id acta */
  actaId: number;
  /** Referencia a la transacción blockchain */
  transaccionRef: string;
  /** Language */
  lang: string;
}
