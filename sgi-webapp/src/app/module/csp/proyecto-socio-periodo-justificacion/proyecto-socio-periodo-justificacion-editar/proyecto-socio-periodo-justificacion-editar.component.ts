import { Component } from '@angular/core';
import { ActionComponent } from '@core/component/action.component';
import { ProyectoSocioPeriodoJustificacionActionService } from '../proyecto-socio-periodo-justificacion.action.service';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES } from '../proyecto-socio-periodo-justificacion-names';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { Router, ActivatedRoute } from '@angular/router';
import { DialogService } from '@core/services/dialog.service';
import { IProyectoSocioState } from '../../proyecto/proyecto-formulario/proyecto-socios/proyecto-socios.component';

const MSG_BUTTON_SAVE = marker('botones.guardar');
const MSG_SUCCESS = marker('csp.proyecto-socio-periodo-justificacion.editar.correcto');
const MSG_ERROR = marker('csp.proyecto-socio-periodo-justificacion.editar.error');

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion-editar',
  templateUrl: './proyecto-socio-periodo-justificacion-editar.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion-editar.component.scss'],
  viewProviders: [
    ProyectoSocioPeriodoJustificacionActionService
  ]
})
export class ProyectoSocioPeriodoJustificacionEditarComponent extends ActionComponent {
  PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES = PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;
  private urlFrom: string;
  private state: IProyectoSocioState;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ProyectoSocioPeriodoJustificacionActionService,
    dialogService: DialogService
  ) {
    super(router, route, actionService, dialogService);
    this.urlFrom = history.state?.urlProyectoSocio;
    this.state = {
      proyectoSocio: history.state?.proyectoSocio,
      proyectoId: history.state?.proyectoId,
      selectedProyectoSocios: history.state?.selectedProyectoSocios,
      urlProyecto: history.state?.urlProyecto
    };
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      () => {
        this.snackBarService.showError(MSG_ERROR);
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS);
        this.router.navigateByUrl(this.urlFrom, {
          state: this.state
        });
      }
    );
  }

  cancel(): void {
    this.router.navigateByUrl(this.urlFrom, {
      state: this.state
    });
  }
}
