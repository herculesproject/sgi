import { Component } from '@angular/core';
import { PROYECTO_PRORROGA_ROUTE_NAMES } from '../proyecto-prorroga-route-names';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute } from '@angular/router';
import { ProyectoProrrogaActionService } from '../proyecto-prorroga.action.service';
import { DialogService } from '@core/services/dialog.service';
import { ActionComponent } from '@core/component/action.component';

const MSG_BUTTON_EDIT = marker('botones.guardar');
const MSG_SUCCESS = marker('csp.proyecto-prorroga.editar.correcto');
const MSG_ERROR = marker('csp.proyecto-prorroga.editar.error');

@Component({
  selector: 'sgi-proyecto-prorroga-editar',
  templateUrl: './proyecto-prorroga-editar.component.html',
  styleUrls: ['./proyecto-prorroga-editar.component.scss'],
  viewProviders: [
    ProyectoProrrogaActionService
  ]
})
export class ProyectoProrrogaEditarComponent extends ActionComponent {

  PROYECTO_PRORROGA_ROUTE_NAMES = PROYECTO_PRORROGA_ROUTE_NAMES;

  textoEditar = MSG_BUTTON_EDIT;
  private urlFrom: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ProyectoProrrogaActionService,
    dialogService: DialogService
  ) {
    super(router, route, actionService, dialogService);
    this.urlFrom = history.state?.from;
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      () => {
        this.snackBarService.showError(MSG_ERROR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.returnUrl();
      }
    );
  }

  cancel(): void {
    this.returnUrl();
  }


  private returnUrl() {
    this.router.navigateByUrl(this.urlFrom);
  }
}
