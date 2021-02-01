import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TipoEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { ActionService } from '@core/services/action-service';
import { ContextoProyectoService } from '@core/services/csp/contexto-proyecto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoEntidadFinanciadoraService } from '@core/services/csp/proyecto-entidad-financiadora.service';
import { ProyectoEntidadGestoraService } from '@core/services/csp/proyecto-entidad-gestora.service';
import { ProyectoEquipoService } from '@core/services/csp/proyecto-equipo.service';
import { ProyectoHitoService } from '@core/services/csp/proyecto-hito.service';
import { ProyectoPaqueteTrabajoService } from '@core/services/csp/proyecto-paquete-trabajo.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoPlazoService } from '@core/services/csp/proyecto-plazo.service';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { ProyectoContextoFragment } from './proyecto-formulario/proyecto-contexto/proyecto-contexto.fragment';
import { ProyectoFichaGeneralFragment } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.fragment';
import { ProyectoEntidadGestoraFragment } from './proyecto-formulario/proyecto-entidad-gestora/proyecto-entidad-gestora.fragment';
import { ProyectoEntidadesConvocantesFragment } from './proyecto-formulario/proyecto-entidades-convocantes/proyecto-entidades-convocantes.fragment';
import { ProyectoEntidadesFinanciadorasFragment } from './proyecto-formulario/proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.fragment';
import { ProyectoEquipoFragment } from './proyecto-formulario/proyecto-equipo/proyecto-equipo.fragment';
import { ProyectoHitosFragment } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.fragment';
import { ProyectoPaqueteTrabajoFragment } from './proyecto-formulario/proyecto-paquete-trabajo/proyecto-paquete-trabajo.fragment';
import { ProyectoPeriodoSeguimientosFragment } from './proyecto-formulario/proyecto-periodo-seguimientos/proyecto-periodo-seguimientos.fragment';
import { ProyectoPlazosFragment } from './proyecto-formulario/proyecto-plazos/proyecto-plazos.fragment';
import { ProyectoProrrogasFragment } from './proyecto-formulario/proyecto-prorrogas/proyecto-prorrogas.fragment';
import { ProyectoSociosFragment } from './proyecto-formulario/proyecto-socios/proyecto-socios.fragment';



@Injectable()
export class ProyectoActionService extends ActionService {

  public readonly FRAGMENT = {
    FICHA_GENERAL: 'ficha-general',
    ENTIDADES_FINANCIADORAS: 'entidades-financiadoras',
    SOCIOS: 'socios',
    HITOS: 'hitos',
    ENTIDADES_CONVOCANTES: 'entidades-convocantes',
    PAQUETE_TRABAJO: 'paquete-trabajo',
    PLAZOS: 'plazos',
    CONTEXTO_PROYECTO: 'contexto-proyecto',
    SEGUIMIENTO_CIENTIFICO: 'seguimiento-cientificos',
    ENTIDAD_GESTORA: 'entidad-gestora',
    EQUIPO_PROYECTO: 'equipo-proyecto',
    PRORROGAS: 'prorrogas'
  };

  private fichaGeneral: ProyectoFichaGeneralFragment;
  private entidadesFinanciadoras: ProyectoEntidadesFinanciadorasFragment;
  private hitos: ProyectoHitosFragment;
  private socios: ProyectoSociosFragment;
  private entidadesConvocantes: ProyectoEntidadesConvocantesFragment;
  private paqueteTrabajo: ProyectoPaqueteTrabajoFragment;
  private paqueteTrabajoValue: boolean;
  private proyectoConvocatoriaValue: IProyecto;
  private plazos: ProyectoPlazosFragment;
  private proyectoContexto: ProyectoContextoFragment;
  private seguimientoCientifico: ProyectoPeriodoSeguimientosFragment;
  private entidadGestora: ProyectoEntidadGestoraFragment;
  private proyectoEquipo: ProyectoEquipoFragment;
  private prorrogas: ProyectoProrrogasFragment;

  proyecto: IProyecto;
  readonly = false;

  get modeloEjecucionId(): number {
    return this.getDatosGeneralesProyecto().modeloEjecucion?.id;
  }

  get proyectoDatosGenerales(): IProyecto {
    return this.getDatosGeneralesProyecto();
  }

  get readOnly(): boolean {
    if (!this.proyecto?.unidadGestion) {
      return true;
    }
    if (!this.proyecto?.activo) {
      return true;
    }
    if (this.proyecto?.estado?.estado === TipoEstadoProyecto.CANCELADO || this.proyecto?.estado?.estado === TipoEstadoProyecto.FINALIZADO) {
      return true;
    }
    return false;
  }

  public disabledAddSocios$ = new BehaviorSubject<boolean>(false);
  get getProyecto(): IProyecto {
    return this.getDatosGeneralesProyecto();
  }

  constructor(
    fb: FormBuilder,
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    private proyectoService: ProyectoService,
    empresaEconomicaService: EmpresaEconomicaService,
    proyectoSocioService: ProyectoSocioService,
    unidadGestionService: UnidadGestionService,
    convocatoriaService: ConvocatoriaService,
    proyectoEntidadFinanciadoraService: ProyectoEntidadFinanciadoraService,
    proyectoHitoService: ProyectoHitoService,
    proyectoPaqueteTrabajoService: ProyectoPaqueteTrabajoService,
    proyectoPlazoService: ProyectoPlazoService,
    contextoProyectoService: ContextoProyectoService,
    proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    documentoService: DocumentoService,
    proyectoEntidadGestora: ProyectoEntidadGestoraService,
    proyectoEquipoService: ProyectoEquipoService,
    personaFisicaService: PersonaFisicaService,
    proyectoProrrogaService: ProyectoProrrogaService
  ) {
    super();

    if (route.snapshot.data.proyecto) {
      this.proyecto = route.snapshot.data.proyecto;
      this.enableEdit();
    }

    this.fichaGeneral = new ProyectoFichaGeneralFragment(logger, fb, this.proyecto?.id,
      proyectoService, unidadGestionService, convocatoriaService, this);

    this.fichaGeneral.paquetesTrabajo$.subscribe((value) => this.paqueteTrabajoValue = Boolean(value));
    this.fichaGeneral.proyectoConvocatoria$.subscribe((value) => this.proyectoConvocatoriaValue = value);

    this.addFragment(this.FRAGMENT.FICHA_GENERAL, this.fichaGeneral);
    if (this.isEdit()) {
      this.entidadesFinanciadoras = new ProyectoEntidadesFinanciadorasFragment(this.proyecto?.id,
        proyectoService, proyectoEntidadFinanciadoraService, empresaEconomicaService, false);
      this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadoras);

      this.socios = new ProyectoSociosFragment(this.proyecto?.id, empresaEconomicaService,
        proyectoService, proyectoSocioService, this);
      this.addFragment(this.FRAGMENT.SOCIOS, this.socios);

      this.hitos = new ProyectoHitosFragment(this.proyecto?.id, proyectoService,
        proyectoHitoService, this.readonly);
      this.addFragment(this.FRAGMENT.HITOS, this.hitos);

      this.plazos = new ProyectoPlazosFragment(this.proyecto?.id, proyectoService, proyectoPlazoService, this.readonly);
      this.addFragment(this.FRAGMENT.PLAZOS, this.plazos);

      this.entidadesConvocantes = new ProyectoEntidadesConvocantesFragment(
        logger, this.proyecto?.id, proyectoService,
        empresaEconomicaService);
      this.addFragment(this.FRAGMENT.ENTIDADES_CONVOCANTES, this.entidadesConvocantes);

      this.paqueteTrabajo = new ProyectoPaqueteTrabajoFragment(this.proyecto?.id, proyectoService,
        proyectoPaqueteTrabajoService, this.readonly);
      this.addFragment(this.FRAGMENT.PAQUETE_TRABAJO, this.paqueteTrabajo);

      this.proyectoContexto = new ProyectoContextoFragment(logger, this.proyecto, proyectoService,
        contextoProyectoService, convocatoriaService, this.proyectoConvocatoriaValue);
      this.addFragment(this.FRAGMENT.CONTEXTO_PROYECTO, this.proyectoContexto);

      this.seguimientoCientifico = new ProyectoPeriodoSeguimientosFragment(this.proyecto?.id, proyectoService,
        proyectoPeriodoSeguimientoService, documentoService, this.proyecto);
      this.addFragment(this.FRAGMENT.SEGUIMIENTO_CIENTIFICO, this.seguimientoCientifico);

      this.proyectoEquipo = new ProyectoEquipoFragment(logger, this.proyecto?.id, proyectoService,
        proyectoEquipoService, personaFisicaService);
      this.addFragment(this.FRAGMENT.EQUIPO_PROYECTO, this.proyectoEquipo);

      this.entidadGestora =
        new ProyectoEntidadGestoraFragment(fb, this.proyecto?.id,
          proyectoService, proyectoEntidadGestora, empresaEconomicaService, this);
      this.addFragment(this.FRAGMENT.ENTIDAD_GESTORA, this.entidadGestora);

      this.prorrogas = new ProyectoProrrogasFragment(this.proyecto?.id, proyectoService,
        proyectoProrrogaService, documentoService, this);
      this.addFragment(this.FRAGMENT.PRORROGAS, this.prorrogas);
    }

  }

  /**
   * Modifica la visibilidad de la pestaña paquete trabajo
   *
   * @param value Valor boolean
   */
  get disabledPaqueteTrabajo(): boolean {
    return this.paqueteTrabajoValue;
  }

  /**
   * Recupera los datos del proyecto del formulario de datos generales,
   * si no se ha cargado el formulario de datos generales se recuperan los datos de la proyecto que se esta editando.
   *
   * @returns los datos generales del proyecto.
   */
  private getDatosGeneralesProyecto(): IProyecto {
    return this.fichaGeneral.isInitialized() ? this.fichaGeneral.getValue() : {} as IProyecto;
  }


  /**
   * Indica si es un proyecto colaborativo
   */
  set isProyectoColaborativo(isColaborativo: boolean) {
    this.disabledAddSocios$.next(isColaborativo);
  }


  /**
   * Comprueba si es un proyecto colaborativo, si la pestaña fichaGeneral no esta inicializada
   * y es una edicion se hace la consulta y si no se recupera el valor previo de isProyectoColavorativo.
   */
  checkProyectoColavorativo(): void {
    if (!this.fichaGeneral.isInitialized() && this.proyecto?.id) {
      const subscription = this.proyectoService.findById(this.proyecto.id)
        .subscribe((proyecto) => {
          this.isProyectoColaborativo = proyecto ? proyecto.colaborativo : false;
        },
          (error) => {
            this.logger.error(error);
            this.isProyectoColaborativo = false;
          }
        );
      this.subscriptions.push(subscription);
    }
  }

}
