import { Component, ViewEncapsulation } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { NGXLogger } from 'ngx-logger';

import { ActionComponent } from '@core/component/action.component';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';

import { ConvocatoriaActionService } from '../convocatoria.action.service';

import { CONVOCATORIA_ROUTE_NAMES } from '../convocatoria-route-names';

import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';


const MSG_BUTTON_EDIT = marker('botones.editar');
const MSG_SUCCESS = marker('csp.convocatoria.editar.correcto');
const MSG_ERROR = marker('csp.convocatoria.editar.error');


@Component({
  selector: 'sgi-convocatoria-editar',
  templateUrl: './convocatoria-editar.component.html',
  styleUrls: ['./convocatoria-editar.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [
    ConvocatoriaActionService,

  ]
})
export class ConvocatoriaEditarComponent extends ActionComponent {
  CONVOCATORIA_ROUTE_NAMES = CONVOCATORIA_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_EDIT;

  constructor(protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly router: Router,
    private route: ActivatedRoute,
    public actionService: ConvocatoriaActionService,
    dialogService: DialogService) {

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
