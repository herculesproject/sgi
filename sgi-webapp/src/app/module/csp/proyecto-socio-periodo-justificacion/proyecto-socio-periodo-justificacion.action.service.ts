import { Injectable } from '@angular/core';
import { ActionService } from '@core/services/action-service';
import { NGXLogger } from 'ngx-logger';
import { IProyectoSocioPeriodoJustificacion } from '@core/models/csp/proyecto-socio-periodo-justificacion';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesFragment } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-datos-generales/proyecto-socio-periodo-justificacion-datos-generales.fragment';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { ProyectoSocioPeriodoJustificacionDocumentosFragment } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-documentos/proyecto-socio-periodo-justificacion-documentos.fragment';
import { SocioPeriodoJustificacionDocumentoService } from '@core/services/csp/socio-periodo-justificacion-documento.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';

@Injectable()
export class ProyectoSocioPeriodoJustificacionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    DOCUMENTOS: 'documentos'
  };

  private proyectoSocio: IProyectoSocio;
  private periodoJustificacion: IProyectoSocioPeriodoJustificacion;
  private selectedPeriodosJustificacion: IProyectoSocioPeriodoJustificacion[];

  private datosGenerales: ProyectoSocioPeriodoJustificacionDatosGeneralesFragment;
  private documentos: ProyectoSocioPeriodoJustificacionDocumentosFragment;

  constructor(
    logger: NGXLogger,
    proyectoSocioPeriodoJustificacionService: ProyectoSocioPeriodoJustificacionService,
    socioPeriodoJustificacionDocumentoService: SocioPeriodoJustificacionDocumentoService,
    documentoService: DocumentoService
  ) {
    super();

    this.proyectoSocio = history.state.proyectoSocio;
    this.periodoJustificacion = history.state.periodoJustificacion;
    this.selectedPeriodosJustificacion = history.state.selectedPeriodosJustificacion;

    if (this.periodoJustificacion?.id) {
      this.enableEdit();
    }

    this.datosGenerales = new ProyectoSocioPeriodoJustificacionDatosGeneralesFragment(logger,
      this.periodoJustificacion?.id, proyectoSocioPeriodoJustificacionService, this.proyectoSocio,
      this.periodoJustificacion, this.selectedPeriodosJustificacion);
    this.documentos = new ProyectoSocioPeriodoJustificacionDocumentosFragment(logger, this.periodoJustificacion?.id,
      proyectoSocioPeriodoJustificacionService, socioPeriodoJustificacionDocumentoService, documentoService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
  }
}
