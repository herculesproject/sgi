import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionDatosGeneralesFragment } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.fragment';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { ConvocatoriaReunionListadoMemoriasFragment } from './convocatoria-reunion-formulario/convocatoria-reunion-listado-memorias/convocatoria-reunion-listado-memorias.fragment';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';

interface DatosAsignacionEvaluacion {
  idComite: number;
  idTipoComvocatoria: number;
  fechaLimite: Date;
}

@Injectable()
export class ConvocatoriaReunionActionService extends ActionService {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    ASIGNACION_MEMORIAS: 'asignacionMemorias'
  };

  private convocatoriaReunion: ConvocatoriaReunion;
  private datosGenerales: ConvocatoriaReunionDatosGeneralesFragment;
  private asignacionMemorias: ConvocatoriaReunionListadoMemoriasFragment;

  constructor(fb: FormBuilder, route: ActivatedRoute, service: ConvocatoriaReunionService, asistenteService: AsistenteService, evaluacionService: EvaluacionService, personaFisicaService: PersonaFisicaService) {
    super();
    this.convocatoriaReunion = new ConvocatoriaReunion();
    if (route.snapshot.data.convocatoriaReunion) {
      this.convocatoriaReunion = route.snapshot.data.convocatoriaReunion;
      this.enableEdit();
    }
    this.datosGenerales = new ConvocatoriaReunionDatosGeneralesFragment(fb, this.convocatoriaReunion?.id, service, asistenteService);
    this.asignacionMemorias = new ConvocatoriaReunionListadoMemoriasFragment(this.convocatoriaReunion?.id, evaluacionService, personaFisicaService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.ASIGNACION_MEMORIAS, this.asignacionMemorias);
  }

  public getDatosAsignacion(): DatosAsignacionEvaluacion {
    return {
      idComite: this.datosGenerales.getFormGroup().controls.comite.value?.id,
      idTipoComvocatoria: this.datosGenerales.getFormGroup().controls.tipoConvocatoriaReunion.value?.id,
      fechaLimite: this.datosGenerales.getFormGroup().controls.fechaLimite.value
    };
  }

  onKeyChange(value: number) {
    this.asignacionMemorias.setKey(value);
    this.asignacionMemorias.setConvocatoriaReunion(this.datosGenerales.getValue());
  }
}
