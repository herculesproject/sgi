import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoPeriodoSeguimientoDatosGeneralesFragment } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-datos-generales/proyecto-periodo-seguimiento-datos-generales.fragment';
import { ProyectoPeriodoSeguimientoDocumentosFragment } from './proyecto-periodo-seguimiento-formulario/proyecto-periodo-seguimiento-documentos/proyecto-periodo-seguimiento-documentos.fragment';
import { ProyectoPeriodoSeguimientoDocumentoService } from '@core/services/csp/proyecto-periodo-seguimiento-documento.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';



@Injectable()
export class ProyectoPeriodoSeguimientoActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    DOCUMENTOS: 'documentos'
  };

  private datosGenerales: ProyectoPeriodoSeguimientoDatosGeneralesFragment;
  private documentos: ProyectoPeriodoSeguimientoDocumentosFragment;

  constructor(
    private logger: NGXLogger,
    route: ActivatedRoute,
    proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    periodoSeguimientoDocumentoService: ProyectoPeriodoSeguimientoDocumentoService,
    documentoService: DocumentoService
  ) {
    super();

    this.logger = logger;

    if (history.state?.proyectoPeriodoSeguimiento?.id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoPeriodoSeguimientoDatosGeneralesFragment(logger, history.state?.proyectoPeriodoSeguimiento?.id, proyectoPeriodoSeguimientoService, history.state?.proyecto, history.state?.selectedProyectoPeriodoSeguimientos, history.state?.readonly);
    this.documentos = new ProyectoPeriodoSeguimientoDocumentosFragment(logger, history.state?.proyectoPeriodoSeguimiento?.id, proyectoPeriodoSeguimientoService, periodoSeguimientoDocumentoService, documentoService, history.state?.proyecto, history.state?.readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
  }

}
