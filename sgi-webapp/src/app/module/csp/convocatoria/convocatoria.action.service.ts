import { Injectable } from '@angular/core';

import { ActionService } from '@core/services/action-service';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';

import { IConvocatoria } from '@core/models/csp/convocatoria';

import { ConvocatoriaDatosGeneralesFragment } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.fragment';
import { ConvocatoriaPeriodosJustificacionFragment } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodo-justificacion.fragment';
import { ConvocatoriaPlazosFasesFragment } from './convocatoria-formulario/convocatoria-plazos-fases/convocatoria-plazos-fases.fragment';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaHitosFragment } from './convocatoria-formulario/convocatoria-hitos/convocatoria-hitos.fragment';
import { ConvocatoriaEntidadesConvocantesFragment } from './convocatoria-formulario/convocatoria-entidades-convocantes/convocatoria-entidades-convocantes.fragment';

@Injectable()
export class ConvocatoriaActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    PERIODO_JUSTIFICACION: 'periodos-justificacion',
    PLAZOS_FASES: 'plazos-fases',
    HITOS: 'hitos',
    ENTIDADES_CONVOCANTES: 'entidades-convocantes'
  };



  private datosGenerales: ConvocatoriaDatosGeneralesFragment;
  private plazosFases: ConvocatoriaPlazosFasesFragment;
  private periodoJustificacion: ConvocatoriaPeriodosJustificacionFragment;
  private hitos: ConvocatoriaHitosFragment;
  private entidadesConvocantes: ConvocatoriaEntidadesConvocantesFragment;


  private convocatoria: IConvocatoria;

  constructor(
    logger: NGXLogger,
    fb: FormBuilder,
    route: ActivatedRoute,
    service: ConvocatoriaService) {
    super();
    this.convocatoria = {} as IConvocatoria;
    if (route.snapshot.data.convocatoria) {
      this.convocatoria = route.snapshot.data.convocatoria;
      this.enableEdit();
    }
    this.datosGenerales = new ConvocatoriaDatosGeneralesFragment(fb, this.convocatoria?.id, service);
    this.periodoJustificacion = new ConvocatoriaPeriodosJustificacionFragment(logger, this.convocatoria?.id, service);
    this.entidadesConvocantes = new ConvocatoriaEntidadesConvocantesFragment(logger, this.convocatoria?.id, service);
    this.plazosFases = new ConvocatoriaPlazosFasesFragment(logger, this.convocatoria?.id, service);
    this.hitos = new ConvocatoriaHitosFragment(this.convocatoria?.id, service);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.ENTIDADES_CONVOCANTES, this.entidadesConvocantes);
    this.addFragment(this.FRAGMENT.PERIODO_JUSTIFICACION, this.periodoJustificacion);
    this.addFragment(this.FRAGMENT.PLAZOS_FASES, this.plazosFases);
    this.addFragment(this.FRAGMENT.HITOS, this.hitos);

  }

}
