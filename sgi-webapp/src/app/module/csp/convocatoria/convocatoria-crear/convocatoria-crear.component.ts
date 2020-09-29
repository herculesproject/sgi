import { Component, ViewEncapsulation } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { NGXLogger } from 'ngx-logger';

import { marker } from '@biesbjerg/ngx-translate-extract-marker';

import { CONVOCATORIA_ROUTE_NAMES } from '../convocatoria-route-names';

import { ActionComponent } from '@core/component/action.component';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DialogService } from '@core/services/dialog.service';

import { ConvocatoriaActionService } from '../convocatoria.action.service';


const MSG_BUTTON_SAVE = marker('botones.guardar');
const MSG_SUCCESS = marker('csp.convocatoria.crear.correcto');
const MSG_ERROR = marker('csp.convocatoria.crear.error');

@Component({
  selector: 'sgi-convocatoria-crear',
  templateUrl: './convocatoria-crear.component.html',
  styleUrls: ['./convocatoria-crear.component.scss'],
  encapsulation: ViewEncapsulation.None,
  viewProviders: [
    ConvocatoriaActionService
  ]
})
export class ConvocatoriaCrearComponent extends ActionComponent {
  CONVOCATORIA_ROUTE_NAMES = CONVOCATORIA_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;

  constructor(protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    public readonly actionService: ConvocatoriaActionService,
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
