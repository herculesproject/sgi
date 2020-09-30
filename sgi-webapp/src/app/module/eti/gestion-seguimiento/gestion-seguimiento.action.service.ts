import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { NGXLogger } from 'ngx-logger';
import { SeguimientoEvaluacionFragment } from '../seguimiento-formulario/seguimiento-evaluacion/seguimiento-evaluacion.fragment';

@Injectable()
export class GestionSeguimientoActionService extends ActionService {

  public readonly FRAGMENT = {
    EVALUACIONES: 'evaluaciones'
  };

  private evaluacion: IEvaluacion;
  private evaluaciones: SeguimientoEvaluacionFragment;

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: EvaluacionService,
    personaFisicaService: PersonaFisicaService) {
    super();
    this.evaluacion = {} as IEvaluacion;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.evaluaciones = new SeguimientoEvaluacionFragment(logger, fb, this.evaluacion?.id, service, personaFisicaService);

    this.addFragment(this.FRAGMENT.EVALUACIONES, this.evaluaciones);
  }

  getEvaluacion(): IEvaluacion {
    this.logger.debug(SeguimientoEvaluacionFragment.name, 'getEvaluacion()', 'start');
    this.logger.debug(SeguimientoEvaluacionFragment.name, 'getEvaluacion()', 'end');
    return this.evaluacion;
  }
}
