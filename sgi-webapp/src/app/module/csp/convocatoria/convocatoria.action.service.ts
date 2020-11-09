import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { ActivatedRoute } from '@angular/router';

import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';

import { IConvocatoria } from '@core/models/csp/convocatoria';

import { ConvocatoriaDatosGeneralesFragment } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.fragment';
import { ConvocatoriaPeriodosJustificacionFragment } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodo-justificacion.fragment';
import { ConvocatoriaPlazosFasesFragment } from './convocatoria-formulario/convocatoria-plazos-fases/convocatoria-plazos-fases.fragment';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaHitosFragment } from './convocatoria-formulario/convocatoria-hitos/convocatoria-hitos.fragment';
import { ConvocatoriaEntidadesConvocantesFragment } from './convocatoria-formulario/convocatoria-entidades-convocantes/convocatoria-entidades-convocantes.fragment';
import { ConvocatoriaSeguimientoCientificoFragment } from './convocatoria-formulario/convocatoria-seguimiento-cientifico/convocatoria-seguimiento-cientifico.fragment';
import { ConvocatoriaEnlaceFragment } from './convocatoria-formulario/convocatoria-enlace/convocatoria-enlace.fragment';
import { ConvocatoriaEntidadesFinanciadorasFragment } from './convocatoria-formulario/convocatoria-entidades-financiadoras/convocatoria-entidades-financiadoras.fragment';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { ConvocatoriaEntidadFinanciadoraService } from '@core/services/csp/convocatoria-entidad-financiadora.service';
import { ConvocatoriaEnlaceService } from '@core/services/csp/convocatoria-enlace.service';
import { ConvocatoriaHitoService } from '@core/services/csp/convocatoria-hito.service';
import { ConvocatoriaRequisitosIPFragment } from './convocatoria-formulario/convocatoria-requisitos-ip/convocatoria-requisitos-ip.fragment';
import { RequisitoIPService } from '@core/services/csp/requisito-ip.service';
import { ConvocatoriaEntidadGestoraService } from '@core/services/csp/convocatoria-entidad-gestora.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { FormBuilder } from '@angular/forms';

@Injectable()
export class ConvocatoriaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    PERIODO_JUSTIFICACION: 'periodos-justificacion',
    PLAZOS_FASES: 'plazos-fases',
    HITOS: 'hitos',
    ENTIDADES_CONVOCANTES: 'entidades-convocantes',
    SEGUIMIENTO_CIENTIFICO: 'seguimiento-cientifico',
    ENLACES: 'enlaces',
    ENTIDADES_FINANCIADORAS: 'entidades-financiadoras',
    REQUISITOS_IP: 'requisitos-ip'
  };

  private datosGenerales: ConvocatoriaDatosGeneralesFragment;
  private plazosFases: ConvocatoriaPlazosFasesFragment;
  private periodoJustificacion: ConvocatoriaPeriodosJustificacionFragment;
  private seguimientoCientifico: ConvocatoriaSeguimientoCientificoFragment;
  private hitos: ConvocatoriaHitosFragment;
  private entidadesConvocantes: ConvocatoriaEntidadesConvocantesFragment;
  private entidadesFinanciadorasFragment: ConvocatoriaEntidadesFinanciadorasFragment;
  private enlaces: ConvocatoriaEnlaceFragment;
  private requisitosIP: ConvocatoriaRequisitosIPFragment;

  private convocatoria: IConvocatoria;

  constructor(
    private fb: FormBuilder,
    logger: NGXLogger,
    route: ActivatedRoute,
    convocatoriaService: ConvocatoriaService,
    convocatoriaEnlaceService: ConvocatoriaEnlaceService,
    empresaEconomicaService: EmpresaEconomicaService,
    convocatoriaEntidadFinanciadoraService: ConvocatoriaEntidadFinanciadoraService,
    convocatoriaHitoService: ConvocatoriaHitoService,
    requisitoIPService: RequisitoIPService,
    convocatoriaEntidadGestoraService: ConvocatoriaEntidadGestoraService,
    unidadGestionService: UnidadGestionService
  ) {
    super();
    this.convocatoria = {} as IConvocatoria;
    if (route.snapshot.data.convocatoria) {
      this.convocatoria = route.snapshot.data.convocatoria;
      this.enableEdit();
    }
    this.datosGenerales = new ConvocatoriaDatosGeneralesFragment(
      logger, this.convocatoria?.id, convocatoriaService, empresaEconomicaService,
      convocatoriaEntidadGestoraService, unidadGestionService);
    this.seguimientoCientifico = new ConvocatoriaSeguimientoCientificoFragment(
      logger, this.convocatoria?.id, convocatoriaService);
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionFragment(
      logger, this.convocatoria?.id, convocatoriaService);
    this.entidadesConvocantes = new ConvocatoriaEntidadesConvocantesFragment(
      logger, this.convocatoria?.id, convocatoriaService);
    this.plazosFases = new ConvocatoriaPlazosFasesFragment(
      logger, this.convocatoria?.id, convocatoriaService);
    this.hitos = new ConvocatoriaHitosFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaHitoService);
    this.entidadesFinanciadorasFragment = new ConvocatoriaEntidadesFinanciadorasFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaEntidadFinanciadoraService);
    this.enlaces = new ConvocatoriaEnlaceFragment(logger, this.convocatoria?.id, convocatoriaService, convocatoriaEnlaceService);
    this.requisitosIP = new ConvocatoriaRequisitosIPFragment(fb, logger, this.convocatoria?.id, requisitoIPService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.SEGUIMIENTO_CIENTIFICO, this.seguimientoCientifico);
    this.addFragment(this.FRAGMENT.ENTIDADES_CONVOCANTES, this.entidadesConvocantes);
    this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadorasFragment);
    this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodoJustificacion);
    this.addFragment(this.FRAGMENT.PLAZOS_FASES, this.plazosFases);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    this.addFragment(this.FRAGMENT.ENLACES, this.enlaces);
    this.addFragment(this.FRAGMENT.REQUISITOS_IP, this.requisitosIP);

  }

}
