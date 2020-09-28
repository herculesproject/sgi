import { Injectable } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { ActionService } from '@core/services/action-service';
import { EquipoTrabajoService } from '@core/services/eti/equipo-trabajo.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { TareaService } from '@core/services/eti/tarea.service';
import { PersonaFisicaService } from '@core/services/sgp/persona-fisica.service';
import { SgiAuthService } from '@sgi/framework/auth/';
import { NGXLogger } from 'ngx-logger';

import {
  EquipoInvestigadorListadoFragment,
} from './peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.fragment';
import { MemoriasListadoFragment } from './peticion-evaluacion-formulario/memorias-listado/memorias-listado.fragment';
import {
  PeticionEvaluacionDatosGeneralesFragment,
} from './peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.fragment';
import {
  PeticionEvaluacionTareasFragment,
} from './peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-listado/peticion-evaluacion-tareas-listado.fragment';




@Injectable()
export class PeticionEvaluacionActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datosGenerales',
    EQUIPO_INVESTIGADOR: 'equipoInvestigador',
    TAREAS: 'tareas',
    MEMORIAS: 'memorias'
  };

  public readonly: boolean;

  private peticionEvaluacion: IPeticionEvaluacion;
  private datosGenerales: PeticionEvaluacionDatosGeneralesFragment;
  private equipoInvestigadorListado: EquipoInvestigadorListadoFragment;
  private tareas: PeticionEvaluacionTareasFragment;
  private memoriasListado: MemoriasListadoFragment;


  constructor(
    fb: FormBuilder,
    protected readonly peticionEvaluacionService: PeticionEvaluacionService,
    private readonly route: ActivatedRoute,
    private readonly sgiAuthService: SgiAuthService,
    protected readonly logger: NGXLogger,
    protected readonly personaFisicaService: PersonaFisicaService,
    protected readonly equipoTrabajoService: EquipoTrabajoService,
    protected readonly tareaService: TareaService
  ) {
    super();

    this.peticionEvaluacion = {} as IPeticionEvaluacion;

    if (route.snapshot.data.peticionEvaluacion) {
      this.peticionEvaluacion = route.snapshot.data.peticionEvaluacion;
      this.enableEdit();
      this.readonly = route.snapshot.data.readonly;
    }

    this.datosGenerales =
      new PeticionEvaluacionDatosGeneralesFragment(
        fb, this.peticionEvaluacion?.id, peticionEvaluacionService, sgiAuthService, this.readonly);
    this.equipoInvestigadorListado = new EquipoInvestigadorListadoFragment(
      this.peticionEvaluacion?.id, logger, personaFisicaService, equipoTrabajoService, peticionEvaluacionService, sgiAuthService);
    this.tareas = new PeticionEvaluacionTareasFragment(this.peticionEvaluacion?.id, personaFisicaService, tareaService);
    this.memoriasListado = new MemoriasListadoFragment(this.peticionEvaluacion?.id, peticionEvaluacionService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.EQUIPO_INVESTIGADOR, this.equipoInvestigadorListado);
    this.addFragment(this.FRAGMENT.TAREAS, this.tareas);
    this.addFragment(this.FRAGMENT.MEMORIAS, this.memoriasListado);
  }

  protected onKeyChange(key: number): void {
    this.datosGenerales.setKey(key);
    // TODO sustituir por setKey
    this.equipoInvestigadorListado.setPeticionEvaluacion(this.datosGenerales.getValue());
    this.tareas.setKey(key);
  }

}

