import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudModalidadService } from '@core/services/csp/solicitud-modalidad.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { EmpresaEconomicaService } from '@core/services/sgp/empresa-economica.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { NGXLogger } from 'ngx-logger';
import { SolicitudDatosGeneralesFragment } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.fragment';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { SolicitudHistoricoEstadosFragment } from './solicitud-formulario/solicitud-historico-estados/solicitud-historico-estados.fragment';
import { SolicitudDocumentosFragment } from './solicitud-formulario/solicitud-documentos/solicitud-documentos.fragment';
import { SolicitudDocumentoService } from '@core/services/csp/solicitud-documento.service';
import { SolicitudHitosFragment } from './solicitud-formulario/solicitud-hitos/solicitud-hitos.fragment';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { SolicitudHitoService } from '@core/services/csp/solicitud-hito.service';
import { SolicitudProyectoFichaGeneralFragment } from './solicitud-formulario/solicitud-proyecto-ficha-general/solicitud-proyecto-ficha-general.fragment';
import { SolicitudProyectoDatosService } from '@core/services/csp/solicitud-proyecto-datos.service';
import { SolicitudEquipoProyectoFragment } from './solicitud-formulario/solicitud-equipo-proyecto/solicitud-equipo-proyecto.fragment';
import { SolicitudProyectoEquipoService } from '@core/services/csp/solicitud-proyecto-equipo.service';
import { switchMap, tap } from 'rxjs/operators';

@Injectable()
export class SolicitudActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    HISTORICO_ESTADOS: 'historicoEstados',
    DOCUMENTOS: 'documentos',
    PROYECTO_DATOS: 'proyectoDatos',
    HITOS: 'hitos',
    EQUIPO_PROYECTO: 'equipoProyecto'
  };

  private datosGenerales: SolicitudDatosGeneralesFragment;
  private historicoEstado: SolicitudHistoricoEstadosFragment;
  private documentos: SolicitudDocumentosFragment;
  private proyectoDatos: SolicitudProyectoFichaGeneralFragment;
  private hitos: SolicitudHitosFragment;
  private equipoProyecto: SolicitudEquipoProyectoFragment;

  solicitud: ISolicitud;

  showHitos$ = new BehaviorSubject<boolean>(false);
  isSociosColaboradores = false;

  constructor(
    private logger: NGXLogger,
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
    solicitudProyectoEquipoService: SolicitudProyectoEquipoService
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
    this.hitos = new SolicitudHitosFragment(logger, this.solicitud?.id, solicitudHitoService, solicitudService);

    if (this.solicitud?.id) {
      solicitudService.hasConvocatoriaSGI(this.solicitud.id).subscribe((hasConvocatoriaSgi) => {
        if (hasConvocatoriaSgi) {
          this.showHitos$.next(true);
        }
      });
    }
    this.historicoEstado = new SolicitudHistoricoEstadosFragment(logger, this.solicitud?.id, solicitudService);
    this.proyectoDatos = new SolicitudProyectoFichaGeneralFragment(logger, this.solicitud, solicitudService,
      solicitudProyectoDatosService, convocatoriaService, this);
    this.equipoProyecto = new SolicitudEquipoProyectoFragment(logger, this.solicitud?.id, solicitudService,
      solicitudProyectoEquipoService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstado);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
    this.addFragment(this.FRAGMENT.PROYECTO_DATOS, this.proyectoDatos);
    this.addFragment(this.FRAGMENT.EQUIPO_PROYECTO, this.equipoProyecto);
  }

  getDatosGeneralesSolicitud(): ISolicitud {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.solicitud : this.solicitud;
  }

  getSolicitantePersonaRef(): string {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.getFormGroup().get('solicitante').value?.personaRef :
      this.solicitud.solicitante?.personaRef;
  }

  existsDatosProyectos(): void {
    this.logger.debug(SolicitudActionService.name, 'existsDatosProyectos()', 'start');
    if (this.proyectoDatos.isInitialized()) {
      this.equipoProyecto.existsDatosProyecto = Boolean(this.proyectoDatos.solicitudProyectoDatos.id) ||
        !this.proyectoDatos.hasErrors();
    } else {
      const id = this.solicitud?.id;
      if (id) {
        const subscription = this.solicitudService.existsSolictudProyectoDatos(id).subscribe(
          res => {
            this.equipoProyecto.existsDatosProyecto = res;
          },
          () => {
            this.equipoProyecto.existsDatosProyecto = false;
          }
        );
        this.subscriptions.push(subscription);
      }
    }
    this.logger.debug(SolicitudActionService.name, 'existsDatosProyectos()', 'end');
  }

  saveOrUpdate(): Observable<void> {
    this.logger.debug(SolicitudActionService.name, 'saveOrUpdate()', 'start');
    this.performChecks(true);
    if (this.hasErrors()) {
      this.logger.error(SolicitudActionService.name, 'saveOrUpdate()', 'error');
      return throwError('Errores');
    }
    if (this.isEdit() && this.proyectoDatos.isInitialized()) {
      return this.proyectoDatos.saveOrUpdate().pipe(
        switchMap(() => {
          this.proyectoDatos.refreshInitialState(true);
          return super.saveOrUpdate();
        }),
        tap(() => this.logger.debug(SolicitudActionService.name,
          'saveOrUpdate()', 'end'))
      );
    } else {
      return super.saveOrUpdate().pipe(
        tap(() => this.logger.debug(SolicitudActionService.name,
          'saveOrUpdate()', 'end'))
      );
    }
  }
}
