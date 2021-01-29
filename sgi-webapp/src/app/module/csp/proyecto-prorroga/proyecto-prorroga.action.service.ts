import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoProrrogaDatosGeneralesFragment } from './proyecto-prorroga-formulario/proyecto-prorroga-datos-generales/proyecto-prorroga-datos-generales.fragment';
import { ProyectoProrrogaDocumentosFragment } from './proyecto-prorroga-formulario/proyecto-prorroga-documentos/proyecto-prorroga-documentos.fragment';
import { ProyectoProrrogaDocumentoService } from '@core/services/csp/proyecto-prorroga-documento.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';



@Injectable()
export class ProyectoProrrogaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    DOCUMENTOS: 'documentos'
  };

  private datosGenerales: ProyectoProrrogaDatosGeneralesFragment;
  private documentos: ProyectoProrrogaDocumentosFragment;

  constructor(
    private logger: NGXLogger,
    route: ActivatedRoute,
    proyectoProrrogaService: ProyectoProrrogaService,
    periodoSeguimientoDocumentoService: ProyectoProrrogaDocumentoService,
    documentoService: DocumentoService
  ) {
    super();

    this.logger = logger;

    if (history.state?.proyectoProrroga?.id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoProrrogaDatosGeneralesFragment(logger, history.state?.proyectoProrroga?.id, proyectoProrrogaService, history.state?.proyecto, history.state?.selectedProyectoProrrogas, history.state?.proyectoProrroga, history.state?.readonly);
    this.documentos = new ProyectoProrrogaDocumentosFragment(logger, history.state?.proyectoProrroga?.id, proyectoProrrogaService, periodoSeguimientoDocumentoService, documentoService, history.state?.proyecto, history.state?.readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
  }

}
