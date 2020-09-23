import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { EvaluacionEvaluacionFragment } from '../evaluacion-formulario/evaluacion-evaluacion/evaluacion-evaluacion.fragment';
import { NGXLogger } from 'ngx-logger';

@Injectable()
export class EvaluacionActionService extends ActionService {

  public readonly FRAGMENT = {
    EVALUACIONES: 'evaluaciones'
  };

  private evaluacion: IEvaluacion;
  private evaluaciones: EvaluacionEvaluacionFragment;

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
    this.evaluaciones = new EvaluacionEvaluacionFragment(logger, fb, this.evaluacion?.id, service, personaFisicaService);

    this.addFragment(this.FRAGMENT.EVALUACIONES, this.evaluaciones);
  }

  getEvaluacion(): IEvaluacion {
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'getEvaluacion()', 'start');
    this.logger.debug(EvaluacionEvaluacionFragment.name, 'getEvaluacion()', 'end');
    return this.evaluacion;
  }
}
