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
import { ConvocatoriaEntidadGestoraService } from '@core/services/csp/convocatoria-entidad-gestora.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { FormBuilder } from '@angular/forms';
import { ConvocatoriaPeriodoJustificacionService } from '@core/services/csp/convocatoria-periodo-justificacion.service';
import { ConvocatoriaFaseService } from '@core/services/csp/convocatoria-fase.service';
import { ConvocatoriaConceptoGastoFragment } from './convocatoria-formulario/convocatoria-concepto-gasto/convocatoria-concepto-gasto.fragment';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ConvocatoriaSeguimientoCientificoService } from '@core/services/csp/convocatoria-seguimiento-cientifico.service';
import { ConvocatoriaAreaTematicaService } from '@core/services/csp/convocatoria-area-tematica.service';
import { ConvocatoriaEntidadConvocanteService } from '@core/services/csp/convocatoria-entidad-convocante.service';
import { ConvocatoriaRequisitosEquipoFragment } from './convocatoria-formulario/convocatoria-requisitos-equipo/convocatoria-requisitos-equipo.fragment';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { ConvocatoriaRequisitoEquipoService } from '@core/services/csp/convocatoria-requisito-equipo.service';


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
    REQUISITOS_IP: 'requisitos-ip',
    ELEGIBILIDAD: 'elegibilidad',
    REQUISITOS_EQUIPO: 'requisitos-equipo'
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
  private elegibilidad: ConvocatoriaConceptoGastoFragment;
  private requisitosEquipo: ConvocatoriaRequisitosEquipoFragment;

  private convocatoria: IConvocatoria;

  constructor(
    fb: FormBuilder,
    logger: NGXLogger,
    route: ActivatedRoute,
    convocatoriaService: ConvocatoriaService,
    convocatoriaEnlaceService: ConvocatoriaEnlaceService,
    empresaEconomicaService: EmpresaEconomicaService,
    convocatoriaEntidadFinanciadoraService: ConvocatoriaEntidadFinanciadoraService,
    convocatoriaEntidadGestoraService: ConvocatoriaEntidadGestoraService,
    unidadGestionService: UnidadGestionService,
    convocatoriaPeriodoJustificacionService: ConvocatoriaPeriodoJustificacionService,
    convocatoriaFaseService: ConvocatoriaFaseService,
    convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    convocatoriaHitoService: ConvocatoriaHitoService,
    convocatoriaSeguimientoCientificoService: ConvocatoriaSeguimientoCientificoService,
    convocatoriaAreaTematicaService: ConvocatoriaAreaTematicaService,
    convocatoriaEntidadConvocanteService: ConvocatoriaEntidadConvocanteService,
    convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoService,
    convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPService,
  ) {
    super();
    this.convocatoria = {} as IConvocatoria;
    if (route.snapshot.data.convocatoria) {
      this.convocatoria = route.snapshot.data.convocatoria;
      this.enableEdit();
    }
    this.datosGenerales = new ConvocatoriaDatosGeneralesFragment(
      logger, this.convocatoria?.id, convocatoriaService, empresaEconomicaService,
      convocatoriaEntidadGestoraService, unidadGestionService, convocatoriaAreaTematicaService);
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionFragment(logger,
      this.convocatoria?.id, convocatoriaService, convocatoriaPeriodoJustificacionService);
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaPeriodoJustificacionService);
    this.entidadesConvocantes = new ConvocatoriaEntidadesConvocantesFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaEntidadConvocanteService,
      empresaEconomicaService);
    this.plazosFases = new ConvocatoriaPlazosFasesFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaFaseService);
    this.hitos = new ConvocatoriaHitosFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaHitoService);
    this.seguimientoCientifico = new ConvocatoriaSeguimientoCientificoFragment(logger, this.convocatoria?.id,
      convocatoriaService, convocatoriaSeguimientoCientificoService);
    this.entidadesFinanciadorasFragment = new ConvocatoriaEntidadesFinanciadorasFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaEntidadFinanciadoraService);
    this.enlaces = new ConvocatoriaEnlaceFragment(logger, this.convocatoria?.id, convocatoriaService, convocatoriaEnlaceService);
    this.requisitosIP = new ConvocatoriaRequisitosIPFragment(fb, logger, this.convocatoria?.id, convocatoriaRequisitoIPService);
    this.elegibilidad = new ConvocatoriaConceptoGastoFragment(fb, logger, this.convocatoria?.id, convocatoriaService,
      convocatoriaConceptoGastoService);
    this.requisitosEquipo = new ConvocatoriaRequisitosEquipoFragment(fb, logger, this.convocatoria?.id, convocatoriaRequisitoEquipoService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.SEGUIMIENTO_CIENTIFICO, this.seguimientoCientifico);
    this.addFragment(this.FRAGMENT.ENTIDADES_CONVOCANTES, this.entidadesConvocantes);
    this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadorasFragment);
    this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodoJustificacion);
    this.addFragment(this.FRAGMENT.PLAZOS_FASES, this.plazosFases);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    this.addFragment(this.FRAGMENT.ENLACES, this.enlaces);
    this.addFragment(this.FRAGMENT.REQUISITOS_IP, this.requisitosIP);
    this.addFragment(this.FRAGMENT.ELEGIBILIDAD, this.elegibilidad);
    this.addFragment(this.FRAGMENT.REQUISITOS_EQUIPO, this.requisitosEquipo);


  }

  /**
   * Recupera los datos de la convocatoria del formulario de datos generales,
   * si no se ha cargado el formulario de datos generales se recuperan los datos de la convocatoria que se esta editando.
   *
   * @returns los datos de la convocatoria.
   */
  getDatosGeneralesConvocatoria(): IConvocatoria {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.getValue() : this.convocatoria;
  }

}
