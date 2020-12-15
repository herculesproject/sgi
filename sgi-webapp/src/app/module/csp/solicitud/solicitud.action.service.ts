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
import { ConvocatoriaDocumentoService } from '@core/services/csp/convocatoria-documento.service';
import { SolicitudDocumentoService } from '@core/services/csp/solicitud-documento.service';
import { SolicitudHitosFragment } from './solicitud-formulario/solicitud-hitos/solicitud-hitos.fragment';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { SolicitudHitoService } from '@core/services/csp/solicitud-hito.service';
import { SolicitudProyectoFichaGeneralFragment } from './solicitud-formulario/solicitud-proyecto-ficha-general/solicitud-proyecto-ficha-general.fragment';
import { SolicitudProyectoDatosService } from '@core/services/csp/solicitud-proyecto-datos.service';
import { switchMap, tap } from 'rxjs/operators';



@Injectable()
export class SolicitudActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    HISTORICO_ESTADOS: 'historicoEstados',
    DOCUMENTOS: 'documentos',
    PROYECTO_DATOS: 'proyectoDatos',
    HITOS: 'hitos',
  };

  private datosGenerales: SolicitudDatosGeneralesFragment;
  private historicoEstado: SolicitudHistoricoEstadosFragment;
  private documentos: SolicitudDocumentosFragment;
  private proyectoDatos: SolicitudProyectoFichaGeneralFragment;
  private hitos: SolicitudHitosFragment;

  solicitud: ISolicitud;

  showHitos$ = new BehaviorSubject<boolean>(false);
  isSociosColaboradores = false;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    solicitudService: SolicitudService,
    configuracionSolicitudService: ConfiguracionSolicitudService,
    convocatoriaService: ConvocatoriaService,
    empresaEconomicaService: EmpresaEconomicaService,
    personaFisicaService: PersonaFisicaService,
    solicitudModalidadService: SolicitudModalidadService,
    solicitudHitoService: SolicitudHitoService,
    unidadGestionService: UnidadGestionService,
    sgiAuthService: SgiAuthService,
    solicitudDocumentoService: SolicitudDocumentoService,
    solicitudProyectoDatosService: SolicitudProyectoDatosService
  ) {
    super();
    this.solicitud = {} as ISolicitud;
    if (route.snapshot.data.solicitud) {
      this.solicitud = route.snapshot.data.solicitud;
      this.enableEdit();
    }

    this.datosGenerales = new SolicitudDatosGeneralesFragment(logger, this.solicitud?.id, solicitudService, configuracionSolicitudService,
      convocatoriaService, empresaEconomicaService, personaFisicaService, solicitudModalidadService, unidadGestionService, sgiAuthService);
    this.documentos = new SolicitudDocumentosFragment(logger, this.solicitud?.id, this.solicitud?.convocatoria?.id,
      configuracionSolicitudService, solicitudService, solicitudDocumentoService);
    this.hitos = new SolicitudHitosFragment(logger, this.solicitud?.id, solicitudHitoService, solicitudService, sgiAuthService);

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

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);
    this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstado);
    this.addFragment(this.FRAGMENT.DOCUMENTOS, this.documentos);
    this.addFragment(this.FRAGMENT.PROYECTO_DATOS, this.proyectoDatos);
  }

  getDatosGeneralesSolicitud(): ISolicitud {
    return this.datosGenerales.isInitialized() ? this.datosGenerales.solicitud : this.solicitud;
  }
}
