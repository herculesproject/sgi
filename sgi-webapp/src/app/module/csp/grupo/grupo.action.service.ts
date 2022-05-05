import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IGrupo } from '@core/models/csp/grupo';
import { ActionService } from '@core/services/action-service';
import { GrupoEnlaceService } from '@core/services/csp/grupo-enlace/grupo-enlace.service';
import { GrupoEquipoInstrumentalService } from '@core/services/csp/grupo-equipo-instrumental/grupo-equipo-instrumental.service';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoPersonaAutorizadaService } from '@core/services/csp/grupo-persona-autorizada/grupo-persona-autorizada.service';
import { GrupoResponsableEconomicoService } from '@core/services/csp/grupo-responsable-economico/grupo-responsable-economico.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { GRUPO_ROUTE_PARAMS } from './autorizacion-route-params';
import { GRUPO_DATA_KEY } from './grupo-data.resolver';
import { GrupoDatosGeneralesFragment } from './grupo-formulario/grupo-datos-generales/grupo-datos-generales.fragment';
import { GrupoEnlaceFragment } from './grupo-formulario/grupo-enlace/grupo-enlace.fragment';
import { GrupoEquipoInstrumentalFragment } from './grupo-formulario/grupo-equipo-instrumental/grupo-equipo-instrumental.fragment';
import { GrupoEquipoInvestigacionFragment } from './grupo-formulario/grupo-equipo-investigacion/grupo-equipo-investigacion.fragment';
import { GrupoPersonaAutorizadaFragment } from './grupo-formulario/grupo-persona-autorizada/grupo-persona-autorizada.fragment';
import { GrupoResponsableEconomicoFragment } from './grupo-formulario/grupo-responsable-economico/grupo-responsable-economico.fragment';

export interface IGrupoData {
  isInvestigador: boolean;
  readonly: boolean;
}

@Injectable()
export class GrupoActionService extends ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    EQUIPO_INVESTIGACION: 'equipo-investigacion',
    RESPONSABLE_ECONOMICO: 'responsable-economico',
    EQUIPO_INSTRUMENTAL: 'equipo-instrumental',
    ENLACE: 'enlace',
    PERSONA_AUTORIZADA: 'persona-autorizada',
  };

  private datosGenerales: GrupoDatosGeneralesFragment;
  private equiposInvestigacion: GrupoEquipoInvestigacionFragment;
  private responsablesEconomicos: GrupoResponsableEconomicoFragment;
  private equiposInstrumentales: GrupoEquipoInstrumentalFragment;
  private enlaces: GrupoEnlaceFragment;
  private personasAutorizadas: GrupoPersonaAutorizadaFragment;

  private readonly data: IGrupoData;
  public readonly id: number;

  get isInvestigador(): boolean {
    return this.data.isInvestigador;
  }

  get grupo(): IGrupo {
    return this.datosGenerales.getValue();
  }

  get readonly(): boolean {
    return this.data?.readonly;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    grupoService: GrupoService,
    grupoEquipoService: GrupoEquipoService,
    palabraClaveService: PalabraClaveService,
    rolProyectoService: RolProyectoService,
    vinculacionService: VinculacionService,
    solicitudService: SolicitudService,
    personaService: PersonaService,
    sgiAuthService: SgiAuthService,
    grupoResponsableEconomicoService: GrupoResponsableEconomicoService,
    grupoEquipoInstrumentalService: GrupoEquipoInstrumentalService,
    grupoEnlaceService: GrupoEnlaceService,
    grupoPersonaAutorizadaService: GrupoPersonaAutorizadaService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(GRUPO_ROUTE_PARAMS.ID));

    if (this.id) {
      this.enableEdit();
      this.data = route.snapshot.data[GRUPO_DATA_KEY];
    }

    this.datosGenerales = new GrupoDatosGeneralesFragment(logger, this.id, grupoService, grupoEquipoService, palabraClaveService, rolProyectoService, vinculacionService, solicitudService, this.data?.readonly);
    this.equiposInvestigacion = new GrupoEquipoInvestigacionFragment(logger, this.id, grupoService, grupoEquipoService, personaService, vinculacionService, sgiAuthService, this.data?.readonly);
    this.responsablesEconomicos = new GrupoResponsableEconomicoFragment(logger, this.id, grupoService, grupoResponsableEconomicoService, personaService, sgiAuthService, this.data?.readonly);
    this.equiposInstrumentales = new GrupoEquipoInstrumentalFragment(logger, this.id, grupoService, grupoEquipoInstrumentalService, this.data?.readonly);
    this.enlaces = new GrupoEnlaceFragment(logger, this.id, grupoService, grupoEnlaceService, this.data?.readonly);
    this.personasAutorizadas = new GrupoPersonaAutorizadaFragment(logger, this.id, grupoService, grupoPersonaAutorizadaService, personaService, sgiAuthService, this.data?.readonly);


    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO_INVESTIGACION, this.equiposInvestigacion);
    this.addFragment(this.FRAGMENT.RESPONSABLE_ECONOMICO, this.responsablesEconomicos);
    this.addFragment(this.FRAGMENT.EQUIPO_INSTRUMENTAL, this.equiposInstrumentales);
    this.addFragment(this.FRAGMENT.ENLACE, this.enlaces);
    this.addFragment(this.FRAGMENT.PERSONA_AUTORIZADA, this.personasAutorizadas);

    this.datosGenerales.initialize();
  }

}
