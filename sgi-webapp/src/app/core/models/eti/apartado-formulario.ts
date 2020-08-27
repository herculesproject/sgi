import { BloqueFormulario } from './bloque-formulario';
import { ComponenteFormulario } from './componente-formulario';

export class ApartadoFormulario {
  /** Id */
  id: number;

  /** Bloque del formulario */
  bloqueFormulario: BloqueFormulario;

  /** Nombre */
  nombre: string;

  /** Apartado padre del formulario */
  apartadoFormularioPadre: ApartadoFormulario;

  /** Orden */
  orden: number;

  /** Componente del formulario */
  componenteFormulario: ComponenteFormulario;

  /** Activo */
  activo: boolean;

  constructor(apartadoFormulario?: ApartadoFormulario) {
    this.id = apartadoFormulario?.id;
    this.bloqueFormulario = apartadoFormulario?.bloqueFormulario;
    this.nombre = apartadoFormulario?.nombre;
    this.apartadoFormularioPadre = apartadoFormulario?.apartadoFormularioPadre;
    this.orden = apartadoFormulario?.orden;
    this.componenteFormulario = apartadoFormulario?.componenteFormulario;
    this.activo = apartadoFormulario?.activo;
  }
}
