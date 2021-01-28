import { Component } from '@angular/core';
import { ProyectoPeriodoSeguimientoActionService } from '../proyecto-periodo-seguimiento.action.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES } from '../proyecto-periodo-seguimiento-route-names';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DialogService } from '@core/services/dialog.service';

const MSG_BUTTON_SAVE = marker('botones.guardar');
const MSG_SUCCESS = marker('csp.proyecto-periodo-seguimiento.crear.correcto');
const MSG_ERROR = marker('csp.proyecto-periodo-seguimiento.crear.error');

@Component({
  selector: 'sgi-proyecto-periodo-seguimiento-crear',
  templateUrl: './proyecto-periodo-seguimiento-crear.component.html',
  styleUrls: ['./proyecto-periodo-seguimiento-crear.component.scss'],
  viewProviders: [
    ProyectoPeriodoSeguimientoActionService
  ]
})
export class ProyectoPeriodoSeguimientoCrearComponent extends ActionComponent {
  PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES = PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;
  private urlFrom: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ProyectoPeriodoSeguimientoActionService,
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
        this.router.navigateByUrl(this.urlFrom);
      }
    );
  }

  cancel(): void {
    this.router.navigateByUrl(this.urlFrom);
  }
}
