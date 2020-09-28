import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { ActionService } from '@core/services/action-service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { EvaluacionComentarioFragment } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.fragment';
import { EvaluacionDatosMemoriaFragment } from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.fragment';
import { EvaluacionDocumentacionFragment } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.fragment';
import { NGXLogger } from 'ngx-logger';


@Injectable()
export class EvaluacionEvaluadorEvaluarActionService extends ActionService {

  public readonly FRAGMENT = {
    COMENTARIOS: 'comentarios',
    MEMORIA: 'memoria',
    DOCUMENTACION: 'documentarion'
  };

  private evaluacion: IEvaluacion;
  private comentarios: EvaluacionComentarioFragment;
  private datosMemoria: EvaluacionDatosMemoriaFragment;
  private documentacion: EvaluacionDocumentacionFragment;

  constructor(
    private readonly logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: EvaluacionService,
    personaFisicaService: PersonaFisicaService
  ) {
    super();
    this.logger.debug(EvaluacionEvaluadorEvaluarActionService.name, 'constructor()', 'start');
    this.evaluacion = {} as IEvaluacion;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.comentarios = new EvaluacionComentarioFragment(this.logger, this.evaluacion?.id, service);
    this.datosMemoria = new EvaluacionDatosMemoriaFragment(this.logger, fb, this.evaluacion?.id, service, personaFisicaService);
    this.documentacion = new EvaluacionDocumentacionFragment(this.logger, this.evaluacion?.id);

    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
    this.addFragment(this.FRAGMENT.MEMORIA, this.datosMemoria);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);
    this.logger.debug(EvaluacionEvaluadorEvaluarActionService.name, 'constructor()', 'end');
  }

  getEvaluacion(): IEvaluacion {
    this.logger.debug(EvaluacionEvaluadorEvaluarActionService.name, 'getEvaluacion()', 'start');
    this.logger.debug(EvaluacionEvaluadorEvaluarActionService.name, 'getEvaluacion()', 'end');
    return this.evaluacion;
  }
}
