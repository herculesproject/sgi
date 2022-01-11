import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AUTORIZACION_ROUTE_NAMES } from '../autorizacion-route-names';
import { AutorizacionActionService } from '../autorizacion.action.service';

const AUTORIZACION_KEY = marker('csp.autorizacion');
const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const MSG_SUCCESS_PRESENTAR = marker('msg.csp.autorizacion.presentar.success');
const MSG_ERROR_PRESENTAR = marker('error.csp.autorizacion.presentar');
const MSG_BUTTON_PRESENTAR = marker('csp.autorizacion.presentar');

@Component({
  selector: 'sgi-autorizacion-editar',
  templateUrl: './autorizacion-editar.component.html',
  styleUrls: ['./autorizacion-editar.component.scss'],
  viewProviders: [
    AutorizacionActionService
  ]
})
export class AutorizacionEditarComponent extends ActionComponent implements OnInit {
  AUTORIZACION_ROUTE_NAMES = AUTORIZACION_ROUTE_NAMES;

  textoEditar: string;
  textoEditarSuccess: string;
  textoEditarError: string;
  textoPresentar = MSG_BUTTON_PRESENTAR;
  disablePresentar$: Subject<boolean> = new BehaviorSubject<boolean>(true);
  private presentable = false;
  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: AutorizacionActionService,
    private autorizacionService: AutorizacionService,
    dialogService: DialogService,
    private readonly translate: TranslateService) {
    super(router, route, actionService, dialogService);

    this.subscriptions.push(
      this.autorizacionService.presentable(this.actionService.id).subscribe(
        presentable => {
          this.presentable = presentable;
          this.disablePresentar$.next(!presentable);
        }
      )
    );
    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disablePresentar$.next(!this.presentable || status.changes || status.errors);
      }
    ));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  setupI18N() {
    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEditar = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEditarSuccess = value);

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEditarError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => {
        // This is intentional
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
          if (!!!error.managed) {
            this.snackBarService.showError(error);
          }
        }
        else {
          this.snackBarService.showError(this.textoEditarError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoEditarSuccess);
        const autorizacionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${autorizacionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

  presentar(): void {
    this.actionService.presentar().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
          this.snackBarService.showError(error);
        }
        else {
          this.snackBarService.showError(MSG_ERROR_PRESENTAR);
        }
      },
      () => {
        this.snackBarService.showSuccess(MSG_SUCCESS_PRESENTAR);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }

}
