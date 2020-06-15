import { FormGroup } from '@angular/forms';
import { FormGroupUtil } from '@shared/config/form-group-util';

/**
 * Clase que simula a las unidades de medida
 */
export class UnidadMedida {
  id: number;
  abreviatura: string;
  descripcion: string;

  constructor() {
    this.id = null;
    this.abreviatura = '';
    this.descripcion = '';
  }
}
