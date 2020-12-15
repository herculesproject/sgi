import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActionComponent } from '@core/component/action.component';
import { SOLICITUD_ROUTE_NAMES } from '../solicitud-route-names';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DialogService } from '@core/services/dialog.service';
import { SolicitudActionService } from '../solicitud.action.service';
import { TipoFormularioSolicitud } from '@core/enums/tipo-formulario-solicitud';

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

  tipoEstandar = false;
  disableRegistrar = false;


  constructor(
    protected logger: NGXLogger,
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
   * Comprueba que el tipo de la solcitud sea "Estándar"
   */
  isTipoEstandar(): void {
    this.logger.debug(SolicitudEditarComponent.name, 'isTipoEstandar()', 'start');
    const formularioSolicitud = this.actionService.getDatosGeneralesSolicitud().formularioSolicitud;
    this.tipoEstandar = formularioSolicitud === TipoFormularioSolicitud.ESTANDAR;
    this.logger.debug(SolicitudEditarComponent.name, 'isTipoEstandar()', 'end');
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
    // TODO: Implementar
  }

}
