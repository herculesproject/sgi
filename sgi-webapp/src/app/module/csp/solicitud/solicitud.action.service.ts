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



@Injectable()
export class SolicitudActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales'
  };

  private datosGenerales: SolicitudDatosGeneralesFragment;


  private solicitud: ISolicitud;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    solicitudService: SolicitudService,
    configuracionSolicitudService: ConfiguracionSolicitudService,
    convocatoriaService: ConvocatoriaService,
    empresaEconomicaService: EmpresaEconomicaService,
    personaFisicaService: PersonaFisicaService,
    solicitudModalidadService: SolicitudModalidadService,
    unidadGestionService: UnidadGestionService,
    sgiAuthService: SgiAuthService
  ) {
    super();
    this.solicitud = {} as ISolicitud;
    if (route.snapshot.data.solicitud) {
      this.solicitud = route.snapshot.data.solicitud;
      this.enableEdit();
    }

    this.datosGenerales = new SolicitudDatosGeneralesFragment(logger, this.solicitud?.id, solicitudService, configuracionSolicitudService,
      convocatoriaService, empresaEconomicaService, personaFisicaService, solicitudModalidadService, unidadGestionService, sgiAuthService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

  }

}
