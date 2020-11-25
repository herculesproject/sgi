import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NGXLogger } from 'ngx-logger';
import { ActionComponent } from '@core/component/action.component';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ConvocatoriaActionService } from '../convocatoria.action.service';
import { CONVOCATORIA_ROUTE_NAMES } from '../convocatoria-route-names';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TipoEstadoConvocatoria } from '@core/enums/tipo-estado-convocatoria';

const MSG_BUTTON_EDIT = marker('botones.guardar');
const MSG_BUTTON_REGISTRAR = marker('csp.convocatoria.registrar');
const MSG_SUCCESS = marker('csp.convocatoria.editar.correcto');
const MSG_ERROR = marker('csp.convocatoria.editar.error');
const MSG_SUCCESS_REGISTRAR = marker('csp.convocatoria.registrar.correcto');
const MSG_ERROR_REGISTRAR = marker('csp.convocatoria.registrar.error');

@Component({
  selector: 'sgi-convocatoria-editar',
  templateUrl: './convocatoria-editar.component.html',
  styleUrls: ['./convocatoria-editar.component.scss'],
  providers: [
    ConvocatoriaActionService
  ]
})
export class ConvocatoriaEditarComponent extends ActionComponent implements OnInit {
  CONVOCATORIA_ROUTE_NAMES = CONVOCATORIA_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_EDIT;
  textoRegistrar = MSG_BUTTON_REGISTRAR;

  disableRegistrar = false;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaActionService,
    dialogService: DialogService) {

    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disableRegistrar = status.changes || status.errors
          || this.isDisableRegistrar();
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
    this.logger.debug(ConvocatoriaEditarComponent.name, 'registrar()', 'start');
    this.actionService.registrar().subscribe(
      () => { },
      () => {
        this.snackBarService.showError(MSG_ERROR_REGISTRAR);
        this.logger.debug(ConvocatoriaEditarComponent.name, 'registrar()', 'end');
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS_REGISTRAR);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
        this.logger.debug(ConvocatoriaEditarComponent.name, 'registrar()', 'end');
      }
    );
  }

  /**
   * Permite habilitar o deshabilitar el botón de registro en función de los datos de convocatoria
   */
  private isDisableRegistrar(): boolean {
    // Se deshabilita el botón de registrar si la convocatoria está en estado registrada
    const convocatoria = this.actionService.getDatosGeneralesConvocatoria();
    if (convocatoria.estadoActual === TipoEstadoConvocatoria.REGISTRADA) {
      return true;
    }

    // a parte de los campos obligatoris, deben estar
    if (!convocatoria.modeloEjecucion || !convocatoria.finalidad || !convocatoria.ambitoGeografico) {
      return true;
    }

    // Se obtienen los datos de la pestaña configuración solicitud y se realiza la validación oportuna
    // sobre los campos correspondientes para saber si se puede registrar o no la convocatoria
    const configuracionSolicitudes = this.actionService.getConfiguracionSolicitudesConvocatoria();
    if (configuracionSolicitudes) {
      // Si el campo de habilitar presentación de solicitudes es afirmativo
      // se comprueba que el campo de presentación de solicitudes esté relleno
      if (configuracionSolicitudes.tramitacionSGI) {
        return !Boolean(configuracionSolicitudes.fasePresentacionSolicitudes);
      }
      // El campo de tipo de baremación debe estar cumplimentado
      // El campo de tipo de formulario debe estar cumplimentado
      return !Boolean(configuracionSolicitudes.baremacionRef) || !Boolean(configuracionSolicitudes.formularioSolicitud);
    }
    return false;
  }

}
