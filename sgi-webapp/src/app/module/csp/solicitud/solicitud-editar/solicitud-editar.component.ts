import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { TipoFormularioSolicitud } from '@core/enums/tipo-formulario-solicitud';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { SOLICITUD_ROUTE_NAMES } from '../solicitud-route-names';
import { SolicitudActionService } from '../solicitud.action.service';

const MSG_BUTTON_EDIT = marker('botones.guardar');
const MSG_BUTTON_REGISTRAR = marker('csp.solicitud.registrar');
const MSG_SUCCESS = marker('csp.solicitud.editar.correcto');
const MSG_ERROR = marker('csp.solicitud.editar.error');

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

  tipoEstandar = false;
  disableRegistrar = false;
  private from: string;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SolicitudActionService,
    dialogService: DialogService) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.isTipoEstandar();
    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disableRegistrar = status.changes || status.errors;
      }
    ));
  }

  /**
   * Comprueba que el tipo de la solcitud sea 'Estándar'
   */
  isTipoEstandar(): void {
    const formularioSolicitud = this.actionService.getDatosGeneralesSolicitud().formularioSolicitud;
    this.tipoEstandar = formularioSolicitud === TipoFormularioSolicitud.ESTANDAR;
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }


  registrar(): void {
    // TODO: Implementar
  }

}
