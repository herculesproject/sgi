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
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';

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

  disableRegistrar = true;
  disable = true;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaActionService,
    dialogService: DialogService,
    private convocatoriaService: ConvocatoriaService
  ) {

    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.isDisableRegistrar();
    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        console.warn(this.disable)
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
  private isDisableRegistrar(): void {
    this.logger.debug(ConvocatoriaEditarComponent.name, 'isDisableRegistrar()', 'start');
    this.subscriptions.push(
      this.convocatoriaService.findById(this.actionService.convocatoriaId).subscribe(
        (convocatoria) => {
          // Se deshabilita el botón de registrar si la convocatoria está en estado registrada
          if (convocatoria.estadoActual === TipoEstadoConvocatoria.REGISTRADA) {
            this.disable = true;
            this.logger.debug(ConvocatoriaEditarComponent.name, 'isDisableRegistrar()', 'end');
            return;
          }

          // a parte de los campos obligatoris, deben estar
          if (!convocatoria.modeloEjecucion || !convocatoria.finalidad || !convocatoria.ambitoGeografico) {
            this.disable = true;
            this.logger.debug(ConvocatoriaEditarComponent.name, 'isDisableRegistrar()', 'end');
            return;
          }

          // Se obtienen los datos de la pestaña configuración solicitud y se realiza la validación oportuna
          // sobre los campos correspondientes para saber si se puede registrar o no la convocatoria
          const configuracionSolicitudes = this.actionService.getConfiguracionSolicitudesConvocatoria();
          if (configuracionSolicitudes) {
            // Si el campo de habilitar presentación de solicitudes es afirmativo
            // se comprueba que el campo de presentación de solicitudes esté relleno
            if (configuracionSolicitudes.tramitacionSGI && !configuracionSolicitudes.fasePresentacionSolicitudes) {
              this.disable = true;
              this.logger.debug(ConvocatoriaEditarComponent.name, 'isDisableRegistrar()', 'end');
              return;
            }
            // El campo de tipo de baremación debe estar cumplimentado
            // El campo de tipo de formulario debe estar cumplimentado
            this.disable = !Boolean(configuracionSolicitudes.baremacionRef) ||
              !Boolean(configuracionSolicitudes.formularioSolicitud);
            this.logger.debug(ConvocatoriaEditarComponent.name, 'isDisableRegistrar()', 'end');
            return;
          }
          this.disable = false;
          this.logger.debug(ConvocatoriaEditarComponent.name, 'isDisableRegistrar()', 'end');
        },
        () => {
          this.disable = true;
          this.logger.error(ConvocatoriaEditarComponent.name, 'isDisableRegistrar()', 'error');
        }
      )
    );
  }

}
