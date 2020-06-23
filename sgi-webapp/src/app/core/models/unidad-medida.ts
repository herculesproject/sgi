/**
 * Clase que simula a las unidades de medida
 */
export class UnidadMedida {
  id: number;
  abreviatura: string;
  descripcion: string;
  activo: boolean;

  constructor() {
    this.id = null;
    this.abreviatura = '';
    this.descripcion = '';
    this.activo = true;
  }
}
