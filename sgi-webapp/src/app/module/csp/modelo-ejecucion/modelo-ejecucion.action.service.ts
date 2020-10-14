import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { ActivatedRoute } from '@angular/router';

import { NGXLogger } from 'ngx-logger';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloEjecucionDatosGeneralesFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-datos-generales/modelo-ejecucion-datos-generales.fragment';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { ModeloEjecucionTipoEnlaceFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-enlace/modelo-ejecucion-tipo-enlace.fragment';
import { ModeloTipoEnlaceService } from '@core/services/csp/modelo-tipo-enlace.service';
import { ModeloEjecucionTipoFinalidadFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-finalidad/modelo-ejecucion-tipo-finalidad.fragment';
import { ModeloTipoFinalidadService } from '@core/services/csp/modelo-tipo-finalidad.service';
import { ModeloEjecucionTipoFaseFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-fase/modelo-ejecucion-tipo-fase.fragment';
import { ModeloTipoFaseService } from '@core/services/csp/modelo-tipo-fase.service';

@Injectable()
export class ModeloEjecucionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    TIPO_ENLACES: 'tipo-enlaces',
    TIPO_FINALIDADES: 'tipo-finalidades',
    TIPO_FASES: 'tipo-fases',
  };

  private modeloEjecucion: IModeloEjecucion;
  private datosGenerales: ModeloEjecucionDatosGeneralesFragment;
  private tipoEnlaces: ModeloEjecucionTipoEnlaceFragment;
  private tipoFinalidades: ModeloEjecucionTipoFinalidadFragment;
  private tipoFases: ModeloEjecucionTipoFaseFragment;

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    modeloEjecucionService: ModeloEjecucionService,
    modeloTipoEnlaceService: ModeloTipoEnlaceService,
    modeloTipoFinalidadService: ModeloTipoFinalidadService,
    modeloTipoFaseService: ModeloTipoFaseService) {
    super();
    this.logger.debug(ModeloEjecucionActionService.name, 'constructor()', 'start');
    this.modeloEjecucion = {} as IModeloEjecucion;
    if (route.snapshot.data.modeloEjecucion) {
      this.modeloEjecucion = route.snapshot.data.modeloEjecucion;
      this.enableEdit();
    }
    this.datosGenerales = new ModeloEjecucionDatosGeneralesFragment(logger, this.modeloEjecucion?.id, modeloEjecucionService, this);
    this.tipoEnlaces = new ModeloEjecucionTipoEnlaceFragment(logger, this.modeloEjecucion?.id,
      modeloEjecucionService, modeloTipoEnlaceService, this);
    this.tipoFinalidades = new ModeloEjecucionTipoFinalidadFragment(logger, this.modeloEjecucion?.id,
      modeloEjecucionService, modeloTipoFinalidadService, this);
    this.tipoFases = new ModeloEjecucionTipoFaseFragment(logger, this.modeloEjecucion?.id, modeloEjecucionService,
      modeloTipoFaseService, this);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.TIPO_ENLACES, this.tipoEnlaces);
    this.addFragment(this.FRAGMENT.TIPO_FASES, this.tipoFases);
    this.addFragment(this.FRAGMENT.TIPO_FINALIDADES, this.tipoFinalidades);
    this.logger.debug(ModeloEjecucionActionService.name, 'constructor()', 'end');
  }
}
