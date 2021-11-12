import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IReparto } from '@core/models/pii/reparto';
import { ActionService } from '@core/services/action-service';
import { InvencionGastoService } from '@core/services/pii/invencion/invencion-gasto/invencion-gasto.service';
import { InvencionIngresoService } from '@core/services/pii/invencion/invencion-ingreso/invencion-ingreso.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RepartoService } from '@core/services/pii/reparto/reparto.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { GastosInvencionService } from '@core/services/sgepii/gastos-invencion.service';
import { IngresosInvencionService } from '@core/services/sgepii/ingresos-invencion.service';
import { IInvencionData } from '../invencion/invencion.action.service';
import { INVENCION_DATA_KEY } from '../invencion/invencion.resolver';
import { INVENCION_REPARTO_DATA_KEY } from './invencion-reparto-data.resolver';
import { InvencionRepartoDatosGeneralesFragment } from './invencion-reparto-formulario/invencion-reparto-datos-generales/invencion-reparto-datos-generales.fragment';
import { InvencionRepartoEquipoInventorFragment } from './invencion-reparto-formulario/invencion-reparto-equipo-inventor/invencion-reparto-equipo-inventor.fragment';
import { INVENCION_REPARTO_ROUTE_PARAMS } from './invencion-reparto-route-params';

export interface IInvencionRepartoData {
  canEdit: boolean;
  reparto: IReparto;
}

@Injectable()
export class InvencionRepartoActionService extends ActionService {

  public readonly id: number;
  private data: IInvencionRepartoData;
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    REPARTO_EQUIPO_INVENTOR: 'reparto-equipo-inventor'
  };

  private datosGenerales: InvencionRepartoDatosGeneralesFragment;
  private repartoEquipoInventor: InvencionRepartoEquipoInventorFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    readonly route: ActivatedRoute,
    readonly repartoService: RepartoService,
    readonly invencionService: InvencionService,
    readonly gastosInvencionService: GastosInvencionService,
    readonly solicitudProteccionService: SolicitudProteccionService,
    readonly ingresosInvencionService: IngresosInvencionService,
    readonly invencionGastoService: InvencionGastoService,
    readonly invencionIngresoService: InvencionIngresoService
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(INVENCION_REPARTO_ROUTE_PARAMS.ID));
    const { invencion }: IInvencionData = route.snapshot.parent.data[INVENCION_DATA_KEY];

    if (this.id) {
      this.data = route.snapshot.data[INVENCION_REPARTO_DATA_KEY];
      this.enableEdit();
    }

    if (this.isEdit()) {
      this.repartoEquipoInventor = new InvencionRepartoEquipoInventorFragment(
        invencion, this.data.reparto, repartoService,
        gastosInvencionService, solicitudProteccionService,
        ingresosInvencionService, invencionGastoService, invencionIngresoService);
      this.addFragment(this.FRAGMENT.REPARTO_EQUIPO_INVENTOR, this.repartoEquipoInventor);
    } else {
      this.datosGenerales = new InvencionRepartoDatosGeneralesFragment(
        invencion, undefined, repartoService,
        invencionService, gastosInvencionService, solicitudProteccionService,
        ingresosInvencionService);
      this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    }

  }
}
