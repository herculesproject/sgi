import { Injectable } from '@angular/core';

import { ActionService, IFragment } from '@core/services/action-service';
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
import { ConvocatoriaDocumentosFragment } from './convocatoria-formulario/convocatoria-documentos/convocatoria-documentos.fragment';
import { ConvocatoriaDocumentoService } from '@core/services/csp/convocatoria-documento.service';
import { ConvocatoriaConfiguracionSolicitudesFragment } from './convocatoria-formulario/convocatoria-configuracion-solicitudes/convocatoria-configuracion-solicitudes.fragment';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { DocumentoRequeridoService } from '@core/services/csp/documento-requerido.service';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Observable, throwError, from, of } from 'rxjs';
import { filter, switchMap, concatMap, tap, takeLast, } from 'rxjs/operators';

import { ConvocatoriaConceptoGastoCodigoEcFragment } from './convocatoria-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec.fragment';
import { ConvocatoriaConceptoGastoCodigoEcService } from '@core/services/csp/convocatoria-concepto-gasto-codigo-ec.service';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { DialogService } from '@core/services/dialog.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthService } from '@sgi/framework/auth';

const MSG_REGISTRAR = marker('csp.convocatoria.registrar.msg');
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
    REQUISITOS_EQUIPO: 'requisitos-equipo',
    CODIGOS_ECONOMICOS: 'codigos-economicos',
    DOCUMENTOS: 'documentos',
    CONFIGURACION_SOLICITUDES: 'configuracion-solicitudes'
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
  private codigosEconomicos: ConvocatoriaConceptoGastoCodigoEcFragment;
  private documentos: ConvocatoriaDocumentosFragment;
  private configuracionSolicitudes: ConvocatoriaConfiguracionSolicitudesFragment;

  private convocatoria: IConvocatoria;
  private dialogService: DialogService;
  private convocatoriaService: ConvocatoriaService;
  private configuracionSolicitud: IConfiguracionSolicitud;

  get modeloEjecucionId(): number {
    return this.getDatosGeneralesConvocatoria().modeloEjecucion?.id;
  }

  get duracion(): number {
    return this.getDatosGeneralesConvocatoria().duracion;
  }

  constructor(
    fb: FormBuilder,
    private logger: NGXLogger,
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
    convocatoriaConceptoGastoCodigoEcService: ConvocatoriaConceptoGastoCodigoEcService,
    convocatoriaDocumentoService: ConvocatoriaDocumentoService,
    configuracionSolicitudService: ConfiguracionSolicitudService,
    documentoRequeridoService: DocumentoRequeridoService,
    dialogService: DialogService,
    private authService: SgiAuthService
  ) {
    super();
    this.convocatoria = {} as IConvocatoria;
    this.dialogService = dialogService;
    this.logger = logger;
    this.convocatoriaService = convocatoriaService;
    if (route.snapshot.data.convocatoria) {
      this.convocatoria = route.snapshot.data.convocatoria;
      this.enableEdit();
    }
    if (route.snapshot.data.configuracionSolicitud) {
      this.configuracionSolicitud = route.snapshot.data.configuracionSolicitud;
    }
    let readonly = false;
    const unidadGestionRef = this.convocatoria.unidadGestionRef;
    if (unidadGestionRef) {
      readonly = !this.authService.hasAuthority(`CSP-CONV-C_${unidadGestionRef}`);
    }
    this.datosGenerales = new ConvocatoriaDatosGeneralesFragment(
      logger, this.convocatoria?.id, convocatoriaService, empresaEconomicaService,
      convocatoriaEntidadGestoraService, unidadGestionService, convocatoriaAreaTematicaService,
      readonly);
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionFragment(logger,
      this.convocatoria?.id, convocatoriaService, convocatoriaPeriodoJustificacionService, readonly);
    this.entidadesConvocantes = new ConvocatoriaEntidadesConvocantesFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaEntidadConvocanteService,
      empresaEconomicaService, readonly);
    this.plazosFases = new ConvocatoriaPlazosFasesFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaFaseService, readonly);
    this.hitos = new ConvocatoriaHitosFragment(logger, this.convocatoria?.id, convocatoriaService,
      convocatoriaHitoService, readonly);
    this.documentos = new ConvocatoriaDocumentosFragment(logger, this.convocatoria?.id, convocatoriaService,
      convocatoriaDocumentoService, readonly);
    this.seguimientoCientifico = new ConvocatoriaSeguimientoCientificoFragment(logger, this.convocatoria?.id,
      convocatoriaService, convocatoriaSeguimientoCientificoService, readonly);
    this.entidadesFinanciadorasFragment = new ConvocatoriaEntidadesFinanciadorasFragment(
      logger, this.convocatoria?.id, convocatoriaService, convocatoriaEntidadFinanciadoraService, readonly);
    this.enlaces = new ConvocatoriaEnlaceFragment(logger, this.convocatoria?.id, convocatoriaService,
      convocatoriaEnlaceService, readonly);
    this.requisitosIP = new ConvocatoriaRequisitosIPFragment(fb, logger, this.convocatoria?.id,
      convocatoriaRequisitoIPService, readonly);
    this.elegibilidad = new ConvocatoriaConceptoGastoFragment(fb, logger, this.convocatoria?.id, convocatoriaService,
      convocatoriaConceptoGastoService, readonly);
    this.requisitosEquipo = new ConvocatoriaRequisitosEquipoFragment(fb, logger, this.convocatoria?.id,
      convocatoriaRequisitoEquipoService, readonly);
    this.codigosEconomicos = new ConvocatoriaConceptoGastoCodigoEcFragment(logger, this.convocatoria?.id,
      convocatoriaService, convocatoriaConceptoGastoCodigoEcService, this.elegibilidad, readonly);
    this.configuracionSolicitudes = new ConvocatoriaConfiguracionSolicitudesFragment(
      logger, this.convocatoria?.id, configuracionSolicitudService, documentoRequeridoService, this, this.plazosFases,
      readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.SEGUIMIENTO_CIENTIFICO, this.seguimientoCientifico);
    this.addFragment(this.FRAGMENT.ENTIDADES_CONVOCANTES, this.entidadesConvocantes);
    this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadorasFragment);
    this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodoJustificacion);
    this.addFragment(this.FRAGMENT.PLAZOS_FASES, this.plazosFases);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
    this.addFragment(this.FRAGMENT.ENLACES, this.enlaces);
    this.addFragment(this.FRAGMENT.REQUISITOS_IP, this.requisitosIP);
    this.addFragment(this.FRAGMENT.ELEGIBILIDAD, this.elegibilidad);
    this.addFragment(this.FRAGMENT.REQUISITOS_EQUIPO, this.requisitosEquipo);
    this.addFragment(this.FRAGMENT.CODIGOS_ECONOMICOS, this.codigosEconomicos);
    this.addFragment(this.FRAGMENT.CONFIGURACION_SOLICITUDES, this.configuracionSolicitudes);
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

  /**
   * Recupera plazos y fases
   * si no se ha cargado el formulario de plazos y fases se recuperan los datos de la convocatoria que se esta editando.
   *
   * @returns los datos de la convocatoria.
   */
  getPlazosFases(): StatusWrapper<IConvocatoriaFase>[] {
    return this.plazosFases.isInitialized() ? this.plazosFases.plazosFase$.value : [];
  }

  /**
   * Recupera los registros de conceptos de gasto permitidos en la convocatoria
   *
   * @returns los conceptos de gastos permitidos de la convocatoria.
   */
  getElegibilidadPermitidos(): StatusWrapper<IConvocatoriaConceptoGasto>[] {
    return this.elegibilidad.isInitialized() ? this.elegibilidad.convocatoriaConceptoGastoPermitido$.value : [];
  }

  /**
   * Recupera los registros de conceptos de gasto no permitidos en la convocatoria
   *
   * @returns los conceptos de gastos no permitidos de la convocatoria.
   */
  getElegibilidadNoPermitidos(): StatusWrapper<IConvocatoriaConceptoGasto>[] {
    return this.elegibilidad.isInitialized() ? this.elegibilidad.convocatoriaConceptoGastoNoPermitido$.value : [];
  }

  /**
   * Recupera los registros de códigos económicos permitidos en la convocatoria
   *
   * @returns los códigos económicos permitidos de la convocatoria.
   */
  private getCodigosEconomicosPermitidos(): StatusWrapper<IConvocatoriaConceptoGastoCodigoEc>[] {
    return this.codigosEconomicos.isInitialized() ? this.codigosEconomicos.convocatoriaConceptoGastoCodigoEcPermitido$.value : [];
  }

  /**
   * Recupera los registros de códigos económicos no permitidos en la convocatoria
   *
   * @returns los códigos económicos no permitidos de la convocatoria.
   */
  private getCodigosEconomicosNoPermitidos(): StatusWrapper<IConvocatoriaConceptoGastoCodigoEc>[] {
    return this.codigosEconomicos.isInitialized() ? this.codigosEconomicos.convocatoriaConceptoGastoCodigoEcNoPermitido$.value : [];
  }


  /**
   * Elimina los códigos de gasto relacionados con el concepto de gasto de la convocatoria a eliminar
   * @param convocatoriaConceptoGasto el concepto de gasto de la convocatoria a eliminar
   */
  deleteCodigoEconomico(convocatoriaConceptoGasto: IConvocatoriaConceptoGasto) {
    if (convocatoriaConceptoGasto.permitido) {
      this.getCodigosEconomicosPermitidos().filter(codigoEconomico =>
        codigoEconomico.value.convocatoriaConceptoGasto.conceptoGasto.id === convocatoriaConceptoGasto.conceptoGasto.id).forEach(
          codEconomicoWrapper => {
            this.codigosEconomicos.deleteConvocatoriaConceptoGastoCodigoEc(codEconomicoWrapper, true);
          });
    } else {
      this.getCodigosEconomicosNoPermitidos().filter(codigoEconomico =>
        codigoEconomico.value.convocatoriaConceptoGasto.conceptoGasto.id === convocatoriaConceptoGasto.conceptoGasto.id).forEach(
          codEconomicoWrapper => {
            this.codigosEconomicos.deleteConvocatoriaConceptoGastoCodigoEc(codEconomicoWrapper, true);
          });
    }
  }

  /**
   * Recupera plazos y fases
   * si no se ha cargado el formulario de plazos y fases se recuperan los datos de la convocatoria que se esta editando.
   *
   * @returns los datos de la convocatoria.
   */
  isPlazosFasesInitialized(): boolean {
    return this.plazosFases.isInitialized();
  }

  /**
   * Cuando se elimina una fase se actualizan los datos de la pestaña configuración solicitudes.
   */
  isDelete(convocatoriaFaseEliminada: IConvocatoriaFase): boolean {
    const fasePresentacionSolicitudes = this.configuracionSolicitudes.getFormGroup()
      .controls.fasePresentacionSolicitudes.value;

    if (!fasePresentacionSolicitudes) {
      return true;
    }

    return !(convocatoriaFaseEliminada.tipoFase.id === fasePresentacionSolicitudes.tipoFase.id
      && convocatoriaFaseEliminada.fechaInicio === fasePresentacionSolicitudes.fechaInicio
      && convocatoriaFaseEliminada.fechaFin === fasePresentacionSolicitudes.fechaFin
      && convocatoriaFaseEliminada.observaciones === fasePresentacionSolicitudes.observaciones);
  }


  initializeConfiguracionSolicitud(): void {
    this.configuracionSolicitudes.initialize();
  }

  initializePlazosFases(): void {
    this.plazosFases.initialize();
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(ConvocatoriaActionService.name, 'saveOrUpdate()', 'start');
    this.performChecks(true);
    if (this.hasErrors()) {
      this.logger.error(ConvocatoriaActionService.name, 'saveOrUpdate()', 'error');
      return throwError('Errores');
    }
    if (this.isEdit()) {
      return this.plazosFases.saveOrUpdate().pipe(
        switchMap(() => {
          this.plazosFases.refreshInitialState(true);
          return super.saveOrUpdate();
        }),
        tap(() => this.logger.debug(ConvocatoriaActionService.name,
          'saveOrUpdate()', 'end'))
      );
    } else {
      return this.datosGenerales.saveOrUpdate().pipe(
        switchMap((key) => {
          this.datosGenerales.refreshInitialState(true);
          if (typeof key === 'string' || typeof key === 'number') {
            this.onKeyChange(key);
          }
          return this.plazosFases.saveOrUpdate();
        }),
        switchMap(() => {
          this.plazosFases.refreshInitialState(true);
          return super.saveOrUpdate();
        }),
        tap(() => this.logger.debug(ConvocatoriaActionService.name,
          'saveOrUpdate()', 'end'))
      );
    }
  }

  /**
   * Recupera los datos de la convocatoria del formulario de configuración de solicitudes
   *
   * @return los datos de la cofiguración de solicitudes.
   */
  getConfiguracionSolicitudesConvocatoria(): IConfiguracionSolicitud {
    return this.configuracionSolicitudes.isInitialized() ? this.configuracionSolicitudes.getValue() : this.configuracionSolicitud;
  }

  /**
   * Acción de registro de una convocatoria
   */
  registrar(): Observable<void> {
    this.logger.debug(ConvocatoriaActionService.name,
      `registrar()`, 'start');
    return this.dialogService.showConfirmation(MSG_REGISTRAR)
      .pipe(switchMap((accept) => {
        if (accept) {
          this.logger.debug(ConvocatoriaActionService.name,
            `registrar()`, 'end');
          return this.convocatoriaService.registrar(this.convocatoria?.id);
        } else {
          this.logger.debug(ConvocatoriaActionService.name,
            `registrar()`, 'end');
          return of(void 0);
        }
      }));
  }
}
