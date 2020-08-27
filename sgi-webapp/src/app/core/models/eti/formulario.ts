export class Formulario {
  /** Id */
  id: number;

  /** Nombre */
  nombre: string;

  /** Descripción */
  descripcion: string;

  /** Activo */
  activo: boolean;

  constructor(formulario?: Formulario) {
    this.id = formulario?.id;
    this.nombre = formulario?.nombre;
    this.descripcion = formulario?.descripcion;
    this.activo = formulario?.activo;
  }
}
