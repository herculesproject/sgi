import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { ActionService } from '@core/services/action-service';
import { GrupoLineaClasificacionService } from '@core/services/csp/grupo-linea-clasificacion/grupo-linea-clasificacion.service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoLineaInvestigadorService } from '@core/services/csp/grupo-linea-investigador/grupo-linea-investigador.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { GrupoLineaClasificacionesFragment } from './grupo-linea-investigacion-formulario/grupo-linea-clasificaciones/grupo-linea-clasificaciones.fragment';
import { GrupoLineaInvestigacionDatosGeneralesFragment } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-datos-generales/grupo-linea-investigacion-datos-generales.fragment';
import { GrupoLineaInvestigadorFragment } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-linea-investigador/grupo-linea-investigador.fragment';

export interface IGrupoLineaInvestigacionData {
  id: number;
  grupo: IGrupo;
  gruposLineasInvestigacion: IGrupoLineaInvestigacion[];
  isInvestigador: boolean;
  readonly: boolean;
}

@Injectable()
export class GrupoLineaInvestigacionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    LINEA_INVESTIGADOR: 'lineaInvestigador',
    CLASIFICACIONES: 'clasificaciones',
  };

  public readonly grupoLineaInvestigacion: IGrupoLineaInvestigacion;
  public readonly: boolean;

  private datosGenerales: GrupoLineaInvestigacionDatosGeneralesFragment;
  private lineasInvestigadores: GrupoLineaInvestigadorFragment;
  private clasificaciones: GrupoLineaClasificacionesFragment;

  get grupoListadoInvestigacion(): IGrupoLineaInvestigacion {
    return this.datosGenerales.getValue();
  }

  constructor(
    logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    personaService: PersonaService,
    sgiAuthService: SgiAuthService,
    service: GrupoLineaInvestigacionService,
    grupoLineaInvestigadorService: GrupoLineaInvestigadorService,
    grupoLineaClasificacionService: GrupoLineaClasificacionService,
    clasificacionService: ClasificacionService,
  ) {
    super();
    this.grupoLineaInvestigacion = {} as IGrupoLineaInvestigacion;
    if (route.snapshot.data.grupoLineaInvestigacionData) {
      this.grupoLineaInvestigacion = route.snapshot.data.grupoLineaInvestigacionData;
      this.enableEdit();
      this.readonly = route.snapshot.data.readonly;
    }

    this.datosGenerales = new GrupoLineaInvestigacionDatosGeneralesFragment(
      fb,
      this.readonly,
      this.grupoLineaInvestigacion?.id,
      this.grupoLineaInvestigacion.grupo,
      service
    );

    this.lineasInvestigadores = new GrupoLineaInvestigadorFragment(
      logger,
      this.grupoLineaInvestigacion?.id,
      this.grupoLineaInvestigacion?.grupo?.id,
      service,
      grupoLineaInvestigadorService,
      personaService,
      sgiAuthService,
      this.readonly
    );

    this.clasificaciones = new GrupoLineaClasificacionesFragment(
      this.grupoLineaInvestigacion?.id,
      grupoLineaClasificacionService,
      service,
      clasificacionService,
      this.readonly
    );

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.LINEA_INVESTIGADOR, this.lineasInvestigadores);
    this.addFragment(this.FRAGMENT.CLASIFICACIONES, this.clasificaciones);
  }

}
