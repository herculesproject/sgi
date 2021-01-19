import { Component } from '@angular/core';
import { SolicitudProyectoSocioActionService } from '../solicitud-proyecto-socio.action.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES } from '../solicitud-proyecto-socio-route-names';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DialogService } from '@core/services/dialog.service';

const MSG_BUTTON_SAVE = marker('botones.guardar');
const MSG_SUCCESS = marker('csp.solicitud-proyecto-socio.crear.correcto');
const MSG_ERROR = marker('csp.solicitud-proyecto-socio.crear.error');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-crear',
  templateUrl: './solicitud-proyecto-socio-crear.component.html',
  styleUrls: ['./solicitud-proyecto-socio-crear.component.scss'],
  viewProviders: [
    SolicitudProyectoSocioActionService
  ]
})
export class SolicitudProyectoSocioCrearComponent extends ActionComponent {
  SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES = SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;
  urlFrom: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SolicitudProyectoSocioActionService,
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
