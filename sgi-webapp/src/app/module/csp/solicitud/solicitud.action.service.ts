import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ActionService } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudDocumentoService } from '@core/services/csp/solicitud-documento.service';
import { SolicitudHitoService } from '@core/services/csp/solicitud-hito.service';
import { SolicitudModalidadService } from '@core/services/csp/solicitud-modalidad.service';
import { SolicitudProyectoDatosService } from '@core/services/csp/solicitud-proyecto-datos.service';
import { SolicitudProyectoEntidadFinanciadoraAjenaService } from '@core/services/csp/solicitud-proyecto-entidad-financiadora-ajena.service';
import { SolicitudProyectoEquipoService } from '@core/services/csp/solicitud-proyecto-equipo.service';
import { SolicitudProyectoPresupuestoService } from '@core/services/csp/solicitud-proyecto-presupuesto.service';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SolicitudDatosGeneralesFragment } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.fragment';
import { SolicitudDocumentosFragment } from './solicitud-formulario/solicitud-documentos/solicitud-documentos.fragment';
import { SolicitudEquipoProyectoFragment } from './solicitud-formulario/solicitud-equipo-proyecto/solicitud-equipo-proyecto.fragment';
import { SolicitudHistoricoEstadosFragment } from './solicitud-formulario/solicitud-historico-estados/solicitud-historico-estados.fragment';
import { SolicitudHitosFragment } from './solicitud-formulario/solicitud-hitos/solicitud-hitos.fragment';
import { SolicitudProyectoEntidadesFinanciadorasFragment } from './solicitud-formulario/solicitud-proyecto-entidades-financiadoras/solicitud-proyecto-entidades-financiadoras.fragment';
import { SolicitudProyectoFichaGeneralFragment } from './solicitud-formulario/solicitud-proyecto-ficha-general/solicitud-proyecto-ficha-general.fragment';
import { SolicitudProyectoPresupuestoGlobalFragment } from './solicitud-formulario/solicitud-proyecto-presupuesto-global/solicitud-proyecto-presupuesto-global.fragment';
import { SolicitudSociosColaboradoresFragment } from './solicitud-formulario/solicitud-socios-colaboradores/solicitud-socios-colaboradores.fragment';


@Injectable()
export class SolicitudActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    HISTORICO_ESTADOS: 'historicoEstados',
    DOCUMENTOS: 'documentos',
    PROYECTO_DATOS: 'proyectoDatos',
    HITOS: 'hitos',
    EQUIPO_PROYECTO: 'equipoProyecto',
    SOCIOS_COLABORADORES: 'sociosColaboradores',
    ENTIDADES_FINANCIADORAS: 'entidadesFinanciadoras',
    DESGLOSE_PRESUPUESTO_GLOBAL: 'desglosePresupuestoGlobal'
  };

  private datosGenerales: SolicitudDatosGeneralesFragment;
  private historicoEstado: SolicitudHistoricoEstadosFragment;
  private documentos: SolicitudDocumentosFragment;
  private proyectoDatos: SolicitudProyectoFichaGeneralFragment;
  private hitos: SolicitudHitosFragment;
  private equipoProyecto: SolicitudEquipoProyectoFragment;
  private socioColaboradores: SolicitudSociosColaboradoresFragment;
  private entidadesFinanciadoras: SolicitudProyectoEntidadesFinanciadorasFragment;
  private desglosePresupuestoGlobal: SolicitudProyectoPresupuestoGlobalFragment;

  solicitud: ISolicitud;
  readonly = false;

  showHitos$ = new BehaviorSubject<boolean>(false);
  isPresupuestoPorEntidades$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    private solicitudService: SolicitudService,
    configuracionSolicitudService: ConfiguracionSolicitudService,
    convocatoriaService: ConvocatoriaService,
    empresaEconomicaService: EmpresaEconomicaService,
    personaFisicaService: PersonaFisicaService,
    solicitudModalidadService: SolicitudModalidadService,
    solicitudHitoService: SolicitudHitoService,
    unidadGestionService: UnidadGestionService,
    sgiAuthService: SgiAuthService,
    solicitudDocumentoService: SolicitudDocumentoService,
    solicitudProyectoDatosService: SolicitudProyectoDatosService,
    solicitudProyectoEquipoService: SolicitudProyectoEquipoService,
    solicitudProyectoSocioService: SolicitudProyectoSocioService,
    solicitudEntidadFinanciadoraService: SolicitudProyectoEntidadFinanciadoraAjenaService,
    solicitudProyectoPresupuestoService: SolicitudProyectoPresupuestoService
  ) {
    super();
    this.solicitud = {
      solicitante: undefined
    } as ISolicitud;
    if (route.snapshot.data.solicitud) {
      this.solicitud = route.snapshot.data.solicitud;
      this.enableEdit();
    }

    this.datosGenerales = new SolicitudDatosGeneralesFragment(logger, this.solicitud?.id, solicitudService, configuracionSolicitudService,
      convocatoriaService, empresaEconomicaService, personaFisicaService, solicitudModalidadService, unidadGestionService, sgiAuthService);
    this.documentos = new SolicitudDocumentosFragment(logger, this.solicitud?.id, this.solicitud?.convocatoria?.id,
      configuracionSolicitudService, solicitudService, solicitudDocumentoService);
    this.hitos = new SolicitudHitosFragment(this.solicitud?.id, solicitudHitoService, solicitudService);

    if (this.solicitud?.id) {
      solicitudService.hasConvocatoriaSGI(this.solicitud.id).subscribe((hasConvocatoriaSgi) => {
        if (hasConvocatoriaSgi) {
          this.showHitos$.next(true);
        }
      });
    }
    this.historicoEstado = new SolicitudHistoricoEstadosFragment(this.solicitud?.id, solicitudService);
    this.proyectoDatos = new SolicitudProyectoFichaGeneralFragment(logger, this.solicitud, solicitudService,
      solicitudProyectoDatosService, convocatoriaService, this);
    this.equipoProyecto = new SolicitudEquipoProyectoFragment(this.solicitud?.id, solicitudService,
      solicitudProyectoEquipoService);
    this.socioColaboradores = new SolicitudSociosColaboradoresFragment(this.solicitud?.id, solicitudService,
      solicitudProyectoSocioService, empresaEconomicaService);
    this.entidadesFinanciadoras = new SolicitudProyectoEntidadesFinanciadorasFragment(this.solicitud?.id, solicitudService,
      solicitudEntidadFinanciadoraService, empresaEconomicaService, this.readonly);
    this.desglosePresupuestoGlobal = new SolicitudProyectoPresupuestoGlobalFragment(this.solicitud?.id, solicitudService,
      solicitudProyectoPresupuestoService, empresaEconomicaService, this.readonly);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstado);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
    this.addFragment(this.FRAGMENT.PROYECTO_DATOS, this.proyectoDatos);
    this.addFragment(this.FRAGMENT.EQUIPO_PROYECTO, this.equipoProyecto);
    this.addFragment(this.FRAGMENT.SOCIOS_COLABORADORES, this.socioColaboradores);
    this.addFragment(this.FRAGMENT.ENTIDADES_FINANCIADORAS, this.entidadesFinanciadoras);
    this.addFragment(this.FRAGMENT.DESGLOSE_PRESUPUESTO_GLOBAL, this.desglosePresupuestoGlobal);

    this.checkSociosColaboradores();
    this.checkPresupuestoPorEntidades();

  }

  getDatosGeneralesSolicitud(): ISolicitud {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.solicitud : this.solicitud;
  }

  getSolicitantePersonaRef(): string {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.getFormGroup().get('solicitante').value?.personaRef :
      this.solicitud.solicitante?.personaRef;
  }

  existsDatosProyectos(): void {
    let existsDatosProyecto$: Observable<boolean>;
    if (this.proyectoDatos.isInitialized()) {
      const existsDatosProyecto = Boolean(this.proyectoDatos.solicitudProyectoDatos.id) ||
        !this.proyectoDatos.hasErrors();
      existsDatosProyecto$ = of(existsDatosProyecto);
    } else if (this.solicitud?.id) {
      existsDatosProyecto$ = this.solicitudService.existsSolictudProyectoDatos(this.solicitud?.id);
    } else {
      existsDatosProyecto$ = of(false);
    }

    const subscription = existsDatosProyecto$.subscribe(
      exists => {
        this.equipoProyecto.existsDatosProyecto = exists;
        this.entidadesFinanciadoras.existsDatosProyecto = exists;
        this.desglosePresupuestoGlobal.existsDatosProyecto = exists;
      },
      () => {
        this.equipoProyecto.existsDatosProyecto = false;
        this.entidadesFinanciadoras.existsDatosProyecto = false;
        this.desglosePresupuestoGlobal.existsDatosProyecto = false;
      }
    );
    this.subscriptions.push(subscription);
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit() && this.proyectoDatos.isInitialized()) {
      return this.proyectoDatos.saveOrUpdate().pipe(
        switchMap(() => {
          this.proyectoDatos.refreshInitialState(true);
          return super.saveOrUpdate();
        })
      );
    } else {
      return super.saveOrUpdate();
    }
  }

  /**
   * Indica si se puede mostrar la pestaña SociosColaboradores
   */
  get isSociosColaboradores(): boolean {
    return this.socioColaboradores.isSociosColaboradores;
  }

  /**
   * Modifica la visibilidad de la pestaña SociosColaboradores
   *
   * @param value Valor boolean
   */
  set sociosColaboradores(value: boolean) {
    this.socioColaboradores.isSociosColaboradores = value;
  }

  /**
   * Habilita la edición de socios colaboradores
   *
   * @param value Valor boolean
   */
  set enableAddSocioColaborador(value: boolean) {
    this.socioColaboradores.enableAddSocioColaborador = value;
  }

  /**
   * Hace la comprobación de mostrar la pestaña SociosColaboradores en el back
   * si no esta inicializada la pestaña ProyectoDatos
   */
  checkSociosColaboradores(): void {
    const id = this.solicitud?.id;
    if (!this.proyectoDatos.isInitialized() && id) {
      const subscription = this.solicitudService.findSolicitudProyectoDatos(id).subscribe(
        (proyectoDatos) => {
          this.sociosColaboradores = proyectoDatos ? proyectoDatos.colaborativo : false;
          this.enableAddSocioColaborador = proyectoDatos ? proyectoDatos.colaborativo : false;
        },
        (error) => {
          this.logger.error(error);
          this.sociosColaboradores = false;
          this.enableAddSocioColaborador = false;
        }
      );
      this.subscriptions.push(subscription);
    }
  }

  checkPresupuestoPorEntidades(): void {
    const subscription = this.solicitudService.hasPresupuestoPorEntidades(this.solicitud.id).subscribe((result) => {
      this.isPresupuestoPorEntidades$.next(result);
    });

    this.subscriptions.push(subscription);
  }

  /**
   * Indica si el presupuesto es por entidades
   */
  set isPresupuestoPorEntidades(isPresupuestoPorEntidades: boolean) {
    this.isPresupuestoPorEntidades$.next(isPresupuestoPorEntidades);
  }

}
