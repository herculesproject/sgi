import { Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';

import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { EvaluacionEvaluarActionService } from '../evaluacion-evaluar.action.service';
import { EVALUACION_ROUTE_NAMES } from '../evaluacion-route-names';
import { DialogService } from '@core/services/dialog.service';

const MSG_BUTTON_SAVE = marker('footer.eti.evaluacion.guardar');
const MSG_SUCCESS = marker('eti.evaluacion.evaluar.correcto');
const MSG_ERROR_SAVE = marker('eti.evaluacion.evaluar.comentarios.error.crear');

@Component({
  selector: 'sgi-evaluacion-evaluar',
  templateUrl: './evaluacion-evaluar.component.html',
  styleUrls: ['./evaluacion-evaluar.component.scss'],
  encapsulation: ViewEncapsulation.None,
  viewProviders: [
    EvaluacionEvaluarActionService
  ]
})
export class EvaluacionEvaluarComponent extends ActionComponent {
  EVALUACION_ROUTE_NAMES = EVALUACION_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    public actionService: EvaluacionEvaluarActionService,
    dialogService: DialogService
  ) {
    super(actionService, dialogService);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      () => {
        this.snackBarService.showError(MSG_ERROR_SAVE);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.router.navigate(['../'], { relativeTo: this.route });
      }
    );
  }
}
