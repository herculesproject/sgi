import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { IEvaluador } from '@core/models/eti/evaluador';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { EvaluadorDatosGeneralesFragment } from './evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.fragment';
import { PersonaService } from '@core/services/sgp/persona.service';

@Injectable()
export class EvaluadorActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales'
  };

  private evaluador: IEvaluador;
  private datosGenerales: EvaluadorDatosGeneralesFragment;

  constructor(fb: FormBuilder, route: ActivatedRoute, service: EvaluadorService, personaService: PersonaService) {
    super();
    this.evaluador = {} as IEvaluador;
    if (route.snapshot.data.evaluador) {
      this.evaluador = route.snapshot.data.evaluador;
      this.enableEdit();
    }
    this.datosGenerales = new EvaluadorDatosGeneralesFragment(fb, this.evaluador?.id, service, personaService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
  }
}
