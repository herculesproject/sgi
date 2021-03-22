import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { IProyectoSocioState } from '../../proyecto/proyecto-formulario/proyecto-socios/proyecto-socios.component';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES } from '../proyecto-socio-periodo-justificacion-names';
import { ProyectoSocioPeriodoJustificacionActionService } from '../proyecto-socio-periodo-justificacion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION = marker('csp.solicitud-proyecto-periodo-justificacion');

@Component({
  selector: 'sgi-proyecto-socio-periodo-justificacion-crear',
  templateUrl: './proyecto-socio-periodo-justificacion-crear.component.html',
  styleUrls: ['./proyecto-socio-periodo-justificacion-crear.component.scss'],
  viewProviders: [
    ProyectoSocioPeriodoJustificacionActionService
  ]
})
export class ProyectoSocioPeriodoJustificacionCrearComponent extends ActionComponent {
  PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES = PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES;

  textoCrear: string;
  textoCrearSuccess: string;
  textoCrearError: string;
  private urlFrom: string;
  private state: IProyectoSocioState;

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ProyectoSocioPeriodoJustificacionActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
    this.urlFrom = history.state?.urlProyectoSocio;
    this.state = {
      proyectoSocio: history.state?.proyectoSocio,
      proyectoId: history.state?.proyectoId,
      coordinadorExterno: history.state?.coordiandorExterno,
      selectedProyectoSocios: history.state?.selectedProyectoSocios,
      urlProyecto: history.state?.urlProyecto
    };
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(this.textoCrearError);
      },
      () => {
        this.snackBarService.showSuccess(this.textoCrearSuccess);
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
