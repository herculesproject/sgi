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
import { SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES } from '../solicitud-proyecto-presupuesto-route-names';
import { SolicitudProyectoPresupuestoActionService } from '../solicitud-proyecto-presupuesto.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const SOLICITUD_PROYECTO_PRESUPUESTO_KEY = marker('csp.solicitud-proyecto-presupuesto');

@Component({
  selector: 'sgi-solicitud-proyecto-presupuesto-crear',
  templateUrl: './solicitud-proyecto-presupuesto-crear.component.html',
  styleUrls: ['./solicitud-proyecto-presupuesto-crear.component.scss'],
  viewProviders: [
    SolicitudProyectoPresupuestoActionService
  ]
})
export class SolicitudProyectoPresupuestoCrearComponent extends ActionComponent {
  SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES = SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;
  textoCrearSuccess: string;
  textoCrearError: string;
  urlFrom: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SolicitudProyectoPresupuestoActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
    this.urlFrom = history.state?.from;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTO_KEY,
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
      SOLICITUD_PROYECTO_PRESUPUESTO_KEY,
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
      SOLICITUD_PROYECTO_PRESUPUESTO_KEY,
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
      () => {
        this.snackBarService.showError(this.textoCrearError);
      },
      () => {
        this.snackBarService.showSuccess(this.textoCrearSuccess);
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
