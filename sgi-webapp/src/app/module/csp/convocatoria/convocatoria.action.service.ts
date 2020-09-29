import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';

import { IConvocatoria } from '@core/models/csp/convocatoria';

import { ConvocatoriaDatosGeneralesFragment } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.fragment';
import { ConvocatoriaPeriodosJustificacionFragment } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodo-justificacion.fragment';
import { NGXLogger } from 'ngx-logger';

@Injectable()
export class ConvocatoriaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    PERIODO_JUSTIFICACION: 'periodos-justificacion'
  };

  private datosGenerales: ConvocatoriaDatosGeneralesFragment;
  private periodoJustificacion: ConvocatoriaPeriodosJustificacionFragment;

  private convocatoria: IConvocatoria;

  constructor(logger: NGXLogger, fb: FormBuilder, route: ActivatedRoute, service: ConvocatoriaService) {
    super();
    this.convocatoria = {} as IConvocatoria;
    if (route.snapshot.data.acta) {
      this.convocatoria = route.snapshot.data.convocatoria;
      this.enableEdit();
    }
    this.datosGenerales = new ConvocatoriaDatosGeneralesFragment(fb, this.convocatoria?.id, service);
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionFragment(logger, this.convocatoria?.id, service);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodoJustificacion);

  }

}
