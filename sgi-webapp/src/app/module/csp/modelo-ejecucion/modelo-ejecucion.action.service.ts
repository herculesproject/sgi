import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { NGXLogger } from 'ngx-logger';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { ModeloEjecucionDatosGeneralesFragment } from './modelo-ejecucion-formulario/modelo-ejecucion-datos-generales/modelo-ejecucion-datos-generales.fragment';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';

@Injectable()
export class ModeloEjecucionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
  };

  private modeloEjecucion: IModeloEjecucion;
  private datosGenerales: ModeloEjecucionDatosGeneralesFragment;

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    modeloEjecucionService: ModeloEjecucionService) {
    super();
    this.logger.debug(ModeloEjecucionActionService.name, 'constructor()', 'start');
    this.modeloEjecucion = {} as IModeloEjecucion;
    if (route.snapshot.data.modeloEjecucion) {
      this.modeloEjecucion = route.snapshot.data.modeloEjecucion;
      this.enableEdit();
    }
    this.datosGenerales = new ModeloEjecucionDatosGeneralesFragment(logger, this.modeloEjecucion?.id, modeloEjecucionService);
    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.logger.debug(ModeloEjecucionActionService.name, 'constructor()', 'end');
  }

}
