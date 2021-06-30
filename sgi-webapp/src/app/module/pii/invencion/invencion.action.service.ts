import { Injectable } from '@angular/core';
import { ActionService } from '@core/services/action-service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { InvencionDatosGeneralesFragment } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.fragment';

@Injectable()
export class InvencionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  }

  private datosGenerales: InvencionDatosGeneralesFragment;

  constructor(
    invencionService: InvencionService
  ) {
    super();
    this.datosGenerales = new InvencionDatosGeneralesFragment(null, invencionService);
    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
  }
}
