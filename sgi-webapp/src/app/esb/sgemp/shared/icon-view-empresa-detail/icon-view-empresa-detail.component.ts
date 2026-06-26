import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { EmpresaFormlyModalComponent, IEmpresaFormlyData } from '../../formly-forms/empresa-formly-modal/empresa-formly-modal.component';

const EMPRESA_KEY = marker('sgemp.empresa');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');

@Component({
  selector: 'sgi-icon-view-empresa-detail',
  templateUrl: './icon-view-empresa-detail.component.html',
  styleUrls: ['./icon-view-empresa-detail.component.scss']
})
export class IconViewEmpresaDetailComponent implements OnInit, OnDestroy {

  @Input()
  empresaId: string;

  @Input()
  get readonly(): boolean {
    return this._readonly;
  }

  set readonly(value: boolean) {
    this._readonly = coerceBooleanProperty(value);
  }

  // tslint:disable-next-line: variable-name
  private _readonly = false;

  @Output()
  readonly empresaUpdated = new EventEmitter<IEmpresa>();

  private textoUpdateSuccess: string;

  protected subscriptions: Subscription[] = [];

  constructor(
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService,
    private readonly matDialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  private setupI18N(): void {
    this.translate.get(
      EMPRESA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

  }

  public openEmpresaFormlyModal(): void {
    const empresaData: IEmpresaFormlyData = {
      empresaId: this.empresaId,
      action: this.resolveActionMode()
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: empresaData
    };
    const dialogRef = this.matDialog.open(EmpresaFormlyModalComponent, config);

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(
        (empresa) => {
          if (empresa) {
            this.empresaUpdated.emit(empresa);
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
          }
        }
      )
    );
  }

  /**
   * Resuelve el modo de apertura del detalle de la empresa.
   *
   * Si no se fuerza el modo solo lectura y el usuario tiene permiso de edición se
   * abre en modo edición. En caso contrario se abre en modo vista, salvo que el
   * usuario no tenga permiso de vista: entonces la única forma de abrir el detalle
   * es el modo edición.
   */
  private resolveActionMode(): ACTION_MODAL_MODE {
    if (!this.readonly && this.hasEditAuthority()) {
      return ACTION_MODAL_MODE.EDIT;
    }

    return this.hasViewAuthority() ? ACTION_MODAL_MODE.VIEW : ACTION_MODAL_MODE.EDIT;
  }

  private hasEditAuthority(): boolean {
    return this.authService.hasAuthority('ESB-EMP-E');
  }

  private hasViewAuthority(): boolean {
    return this.authService.hasAuthority('ESB-EMP-V');
  }

}
