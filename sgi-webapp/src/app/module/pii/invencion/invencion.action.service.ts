import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionDocumentoService } from '@core/services/pii/invencion/invencion-documento/invencion-documento.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { InvencionDatosGeneralesFragment } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.fragment';
import { InvencionDocumentoFragment } from './invencion-formulario/invencion-documento/invencion-documento.fragment';
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
    DOCUMENTOS: 'documentos'
  };

  private datosGenerales: InvencionDatosGeneralesFragment;
  private documentos: InvencionDocumentoFragment;

  get canEdit(): boolean {
    return this.data?.canEdit ?? true;
  }

  constructor(
    invencionService: InvencionService,
    invencionDocumentoService: InvencionDocumentoService,
    route: ActivatedRoute,
    proyectoService: ProyectoService,
    documentoService: DocumentoService,
    areaConocimientoService: AreaConocimientoService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(INVENCION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[INVENCION_DATA_KEY];
      this.enableEdit();
    }

    this.datosGenerales = new InvencionDatosGeneralesFragment(null, this.id, invencionService, proyectoService, areaConocimientoService, this.canEdit);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    if (this.isEdit()) {
      this.documentos = new InvencionDocumentoFragment(this.id, invencionService, invencionDocumentoService, documentoService);
      this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
    }
  }
}
