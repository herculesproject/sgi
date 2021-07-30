import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionDocumentoService } from '@core/services/pii/invencion/invencion-documento/invencion-documento.service';
import { InvencionDocumentoFragment } from './invencion-formulario/invencion-documento/invencion-documento.fragment';
import { InformePatentabilidadService } from '@core/services/pii/informe-patentabilidad/informe-patentabilidad.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { InvencionDatosGeneralesFragment } from './invencion-formulario/invencion-datos-generales/invencion-datos-generales.fragment';
import { InvencionInformesPatentabilidadFragment } from './invencion-formulario/invencion-informes-patentabilidad/invencion-informes-patentabilidad.fragment';
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
    DOCUMENTOS: 'documentos',
    INFORME_PATENTABILIDAD: 'informe-patentabilidad',
  };

  private datosGenerales: InvencionDatosGeneralesFragment;
  private documentos: InvencionDocumentoFragment;
  private informesPatentabilidad: InvencionInformesPatentabilidadFragment;

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
    informePatentabilidadService: InformePatentabilidadService,
    empresaService: EmpresaService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(INVENCION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[INVENCION_DATA_KEY];
      this.enableEdit();
    }

    this.datosGenerales = new InvencionDatosGeneralesFragment(null, this.id, invencionService, proyectoService, areaConocimientoService, invencionDocumentoService, this.canEdit);


    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    if (this.isEdit()) {
      this.documentos = new InvencionDocumentoFragment(this.id, invencionService, invencionDocumentoService, documentoService);
      this.informesPatentabilidad = new InvencionInformesPatentabilidadFragment(
        this.id, this.canEdit, invencionService,
        informePatentabilidadService, documentoService, empresaService
      );

      this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
      this.addFragment(this.FRAGMENT.INFORME_PATENTABILIDAD, this.informesPatentabilidad);
    }
  }
}
