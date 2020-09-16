import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { PeticionEvaluacionDatosGeneralesComponent } from '../peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.component';

import { marker } from '@biesbjerg/ngx-translate-extract-marker';

import { ActionComponent } from '@core/component/action.component';

import { SnackBarService } from '@core/services/snack-bar.service';

import { SgiAuthService } from '@sgi/framework/auth';

import { PeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';


import { PETICION_EVALUACION_ROUTE_NAMES } from '../peticion-evaluacion-route-names';
import { PeticionEvaluacionActionService } from '../peticion-evaluacion.action.service';
import { DialogService } from '@core/services/dialog.service';
import { EquipoInvestigadorListadoComponent } from '../peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.component';

const MSG_BUTTON_SAVE = marker('footer.eti.acta.guardar');
const MSG_ERROR = marker('eti.peticionEvaluacion.crear.error');
const MSG_SUCCESS = marker('eti.peticionEvaluacion.crear.correcto');

@Component({
  selector: 'sgi-peticion-evaluacion-crear',
  templateUrl: './peticion-evaluacion-crear.component.html',
  styleUrls: ['./peticion-evaluacion-crear.component.scss'],
  viewProviders: [
    PeticionEvaluacionActionService
  ]
})
export class PeticionEvaluacionCrearComponent extends ActionComponent {

  PETICION_EVALUACION_ROUTE_NAMES = PETICION_EVALUACION_ROUTE_NAMES;

  @ViewChild('datosGenerales', { static: true }) datosGenerales: PeticionEvaluacionDatosGeneralesComponent;
  @ViewChild('equipoInvestigadorListado', { static: true }) equipoInvestigadorListado: EquipoInvestigadorListadoComponent;

  textoCrear = MSG_BUTTON_SAVE;


  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    public readonly actionService: PeticionEvaluacionActionService,
    dialogService: DialogService
  ) {
    super(actionService, dialogService);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      () => {
        this.snackBarService.showError(MSG_ERROR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.router.navigate(['../'], { relativeTo: this.route });
      }
    );
  }

}
