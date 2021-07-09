import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { InvencionDatosGeneralesFragment } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.fragment';
import { INVENCION_ROUTE_PARAMS } from './invencion-route-params';
import { INVENCION_DATA_KEY } from './invencion.resolver';

export interface IInvencionData {
  canEdit: boolean;
}

@Injectable()
export class InvencionActionService extends ActionService {

  public readonly id: number;
  private data: IInvencionData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  }

  private datosGenerales: InvencionDatosGeneralesFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    invencionService: InvencionService,
    route: ActivatedRoute,
    proyectoService: ProyectoService
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(INVENCION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[INVENCION_DATA_KEY];
      this.enableEdit();
    }

    this.datosGenerales = new InvencionDatosGeneralesFragment(null, this.id, invencionService, proyectoService, this.canEdit);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
  }
}
