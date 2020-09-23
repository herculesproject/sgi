import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { EvaluacionComentarioFragment } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.fragment';
import { EvaluacionDatosMemoriaFragment } from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.fragment';
import { EvaluacionDocumentacionFragment } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.fragment';

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

  constructor(fb: FormBuilder, route: ActivatedRoute, service: EvaluacionService, personaFisicaService: PersonaFisicaService) {
    super();
    this.evaluacion = {} as IEvaluacion;
    if (route.snapshot.data.evaluacion) {
      this.evaluacion = route.snapshot.data.evaluacion;
      this.enableEdit();
    }
    this.comentarios = new EvaluacionComentarioFragment(this.evaluacion?.id, service);
    this.datosMemoria = new EvaluacionDatosMemoriaFragment(fb, this.evaluacion?.id, service, personaFisicaService);
    this.documentacion = new EvaluacionDocumentacionFragment(this.evaluacion?.id);

    this.addFragment(this.FRAGMENT.COMENTARIOS, this.comentarios);
    this.addFragment(this.FRAGMENT.MEMORIA, this.datosMemoria);
    this.addFragment(this.FRAGMENT.DOCUMENTACION, this.documentacion);
  }

  getEvaluacion(): IEvaluacion {
    return this.evaluacion;
  }
}
