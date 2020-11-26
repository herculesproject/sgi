import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActionComponent } from '@core/component/action.component';
import { SOLICITUD_ROUTE_NAMES } from '../solicitud-route-names';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DialogService } from '@core/services/dialog.service';
import { SolicitudActionService } from '../solicitud.action.service';

const MSG_BUTTON_EDIT = marker('botones.guardar');
const MSG_BUTTON_REGISTRAR = marker('csp.convocatoria.registrar');
const MSG_SUCCESS = marker('csp.convocatoria.editar.correcto');
const MSG_ERROR = marker('csp.convocatoria.editar.error');

@Component({
  selector: 'sgi-solicitud-editar',
  templateUrl: './solicitud-editar.component.html',
  styleUrls: ['./solicitud-editar.component.scss'],
  viewProviders: [
    SolicitudActionService
  ]
})
export class SolicitudEditarComponent extends ActionComponent implements OnInit {
  SOLICITUD_ROUTE_NAMES = SOLICITUD_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_EDIT;
  textoRegistrar = MSG_BUTTON_REGISTRAR;

  disableRegistrar = false;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SolicitudActionService,
    dialogService: DialogService) {

    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disableRegistrar = status.changes || status.errors;
      }
    ));
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      () => {
        this.snackBarService.showError(MSG_ERROR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }


  registrar(): void {
    //TODO: Implementar
  }

}
