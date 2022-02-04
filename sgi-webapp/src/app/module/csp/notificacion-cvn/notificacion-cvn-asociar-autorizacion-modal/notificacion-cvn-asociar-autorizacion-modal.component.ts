import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';


const AUTORIZACION_KEY = marker('csp.autorizacion');


@Component({
  selector: 'sgi-notificacion-cvn-asociar-autorizacion-modal',
  templateUrl: './notificacion-cvn-asociar-autorizacion-modal.component.html',
  styleUrls: ['./notificacion-cvn-asociar-autorizacion-modal.component.scss']
})
export class NotificacionCvnAsociarAutorizacionModalComponent extends
  BaseModalComponent<INotificacionProyectoExternoCVN,
  NotificacionCvnAsociarAutorizacionModalComponent> implements OnInit {

  autorizaciones$: Observable<IAutorizacion[]>;
  empresa: IEmpresa;

  fxLayoutProperties: FxLayoutProperties;

  title: string;

  msgParamAutorizacionEntity = {};
  confirmDialogService: any;

  readonly displayerAutorizacion = (autorizacion: IAutorizacion): string => {
    return `${autorizacion?.tituloProyecto} - ${autorizacion.entidad?.nombre}` ?? '';
  }

  constructor(
    private readonly logger: NGXLogger,
    matDialogRef: MatDialogRef<NotificacionCvnAsociarAutorizacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: INotificacionProyectoExternoCVN,
    protected snackBarService: SnackBarService,
    private autorizacionService: AutorizacionService,
    private empresaService: EmpresaService,
    private readonly translate: TranslateService) {
    super(snackBarService, matDialogRef, null);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.autorizaciones$ = this.autorizacionService.
      findAllAutorizadasWithoutNotificacionBySolicitanteRef(this.data.solicitante?.id).pipe(
        switchMap(response =>
          from(response.items).pipe(
            mergeMap(autorizacion => {
              if (autorizacion.entidad?.id) {
                return this.empresaService.findById(autorizacion.entidad?.id).pipe(
                  map((empresa) => {
                    autorizacion.entidad = empresa;
                    return autorizacion;
                  }),
                  catchError((error) => {
                    this.logger.error(error);
                    return EMPTY;
                  }));
              } else {
                return of(autorizacion);
              }
            }),
            toArray(),
            map(() => {
              return response.items;
            })
          )
        )
      );
  }

  private setupI18N(): void {

    this.translate.get(
      AUTORIZACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAutorizacionEntity = { entity: value });
  }
  protected getDatosForm(): INotificacionProyectoExternoCVN {
    this.data.autorizacion = this.formGroup.controls.autorizacion.value;
    return this.data;
  }
  protected getFormGroup(): FormGroup {
    return new FormGroup({
      autorizacion: new FormControl(null, Validators.required)
    });
  }
}
