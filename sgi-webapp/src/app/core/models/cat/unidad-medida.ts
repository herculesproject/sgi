/**
 * Clase que simula a las unidades de medida
 */
export class UnidadMedida {
  /** Id */
  id: number;

  /** Abreviatura */
  abreviatura: string;

  /** Descripción */
  descripcion: string;

  /** Activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.abreviatura = '';
    this.descripcion = '';
    this.activo = true;
  }
}
