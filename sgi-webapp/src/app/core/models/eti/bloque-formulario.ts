import { Formulario } from './formulario';

export class BloqueFormulario {
  /** Id */
  id: number;

  /** Formulario */
  formulario: Formulario;

  /** Nombre */
  nombre: string;

  /** Orden */
  orden: number;

  /** Activo */
  activo: boolean;

  constructor(bloqueFormulario?: BloqueFormulario) {
    this.id = bloqueFormulario?.id;
    this.formulario = bloqueFormulario?.formulario;
    this.nombre = bloqueFormulario?.nombre;
    this.orden = bloqueFormulario?.orden;
    this.activo = bloqueFormulario?.activo;
  }
}
