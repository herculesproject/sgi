import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { ActionService } from '@core/services/action-service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { NGXLogger } from 'ngx-logger';
import { GrupoLineaInvestigacionDatosGeneralesFragment } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-datos-generales/grupo-linea-investigacion-datos-generales.fragment';

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
  };

  public readonly grupoLineaInvestigacion: IGrupoLineaInvestigacion;
  public readonly: boolean;

  private datosGenerales: GrupoLineaInvestigacionDatosGeneralesFragment;

  constructor(
    logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: GrupoLineaInvestigacionService,
  ) {
    super();
    this.grupoLineaInvestigacion = {} as IGrupoLineaInvestigacion;
    if (route.snapshot.data.grupoLineaInvestigacionData) {
      this.grupoLineaInvestigacion = route.snapshot.data.grupoLineaInvestigacionData;
      this.enableEdit();
      this.readonly = route.snapshot.data.readonly;
    }

    this.datosGenerales = new GrupoLineaInvestigacionDatosGeneralesFragment(fb, this.readonly, this.grupoLineaInvestigacion?.id, this.grupoLineaInvestigacion.grupo, service);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

  }
}
