import { Formulario } from './formulario';

export class TipoDocumento {
  /** Id */
  id: number;

  /** Nombre */
  nombre: string;

  /** Formulario */
  formulario: Formulario;

  /** Activo */
  activo: boolean;

  constructor(tipoDocumento?: TipoDocumento) {
    this.id = tipoDocumento?.id;
    this.nombre = tipoDocumento?.nombre;
    this.formulario = tipoDocumento?.formulario;
    this.activo = tipoDocumento?.activo;
  }
}
