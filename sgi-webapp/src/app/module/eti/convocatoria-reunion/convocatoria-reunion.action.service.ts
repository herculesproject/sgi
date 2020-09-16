import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ActionService, IFragment } from '@core/services/action-service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionDatosGeneralesFragment } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.fragment';
import { ConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { ConvocatoriaReunionListadoMemoriasFragment } from './convocatoria-reunion-formulario/convocatoria-reunion-listado-memorias/convocatoria-reunion-listado-memorias.fragment';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { Subject } from 'rxjs';

interface DatosAsignacionEvaluacion {
  idComite: number;
  idTipoConvocatoria: number;
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

  public disableAsignarMemorias: Subject<boolean> = new Subject<boolean>();
  public disableCamposDatosGenerales: Subject<boolean> = new Subject<boolean>();

  constructor(
    fb: FormBuilder,
    route: ActivatedRoute,
    service: ConvocatoriaReunionService,
    asistenteService: AsistenteService,
    evaluacionService: EvaluacionService,
    personaFisicaService: PersonaFisicaService) {

    super();
    this.convocatoriaReunion = new ConvocatoriaReunion();
    if (route.snapshot.data.convocatoriaReunion) {
      this.convocatoriaReunion = route.snapshot.data.convocatoriaReunion;
      this.enableEdit();
    }
    this.datosGenerales = new ConvocatoriaReunionDatosGeneralesFragment(
      fb, this.convocatoriaReunion?.id,
      service, asistenteService);
    this.asignacionMemorias = new ConvocatoriaReunionListadoMemoriasFragment(
      this.convocatoriaReunion?.id,
      evaluacionService, personaFisicaService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.ASIGNACION_MEMORIAS, this.asignacionMemorias);
  }

  public getDatosAsignacion(): DatosAsignacionEvaluacion {
    return {
      idComite: this.datosGenerales.getFormGroup().controls.comite.value?.id,
      idTipoConvocatoria: this.datosGenerales.getFormGroup().controls.tipoConvocatoriaReunion.value?.id,
      fechaLimite: this.datosGenerales.getFormGroup().controls.fechaLimite.value
    };
  }

  onKeyChange(value: number) {
    this.asignacionMemorias.setKey(value);
    this.asignacionMemorias.setConvocatoriaReunion(this.datosGenerales.getValue());
  }

  /**
   * Actualiza el valor de disableCamposDatosGenerales utilizado para evitar que se
   * modifiquen los datos del comité, del tipo de convocatoria y de la fecha límite
   * de la convocatoria cuando ya tiene memorias asignadas.
   */
  onEnterDatosGenerales(): void {
    let disable = true;

    if (this.asignacionMemorias.evaluaciones$.value.length === 0) {
      disable = false;
    }
    this.disableCamposDatosGenerales.next(disable);
  }

  /**
   * Actualiza el valor de disableAsignarMemorias utilizado para evitar que se
   * asignen memorias a la convocatoria cuando aún no se han asignado valores
   * al comité, al tipo de convocatoria y ni a la fecha límite, ya que esos datos
   * son necesarios para determinar las memorias que podrán ser asignadas a la
   * convocatoria.
   */
  onEnterAsignacionMemorias(): void {
    let disable = true;

    if (this.datosGenerales.getFormGroup().controls.comite.value &&
      this.datosGenerales.getFormGroup().controls.tipoConvocatoriaReunion.value &&
      this.datosGenerales.getFormGroup().controls.fechaLimite.value) {
      disable = false;
    }

    this.disableAsignarMemorias.next(disable);
  }
}
