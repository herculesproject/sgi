import { IBloqueFormulario } from './bloque-formulario';
import { IComponenteFormulario } from './componente-formulario';

export interface IApartadoFormulario {
  /** Id */
  id: number;

  /** Bloque del formulario */
  bloqueFormulario: IBloqueFormulario;

  /** Nombre */
  nombre: string;

  /** Apartado padre del formulario */
  apartadoFormularioPadre: IApartadoFormulario;

  /** Orden */
  orden: number;

  /** Componente del formulario */
  componenteFormulario: IComponenteFormulario;

  /** Activo */
  activo: boolean;
}
