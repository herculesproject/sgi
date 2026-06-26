import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IPersona } from '@core/models/sgp/persona';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { IPersonaFormlyData, PersonaFormlyModalComponent } from '../../formly-forms/persona-formly-modal/persona-formly-modal.component';

const PERSONA_KEY = marker('sgp.persona');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');

@Component({
  selector: 'sgi-icon-view-persona-detail',
  templateUrl: './icon-view-persona-detail.component.html',
  styleUrls: ['./icon-view-persona-detail.component.scss']
})
export class IconViewPersonaDetailComponent implements OnInit, OnDestroy {

  @Input()
  personaId: string;

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
  readonly personaUpdated = new EventEmitter<IPersona>();

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
      PERSONA_KEY,
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

  public openPersonaFormlyModal(): void {
    const personaData: IPersonaFormlyData = {
      personaId: this.personaId,
      action: this.resolveActionMode()
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: personaData
    };
    const dialogRef = this.matDialog.open(PersonaFormlyModalComponent, config);

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(
        (persona) => {
          if (persona) {
            this.personaUpdated.emit(persona);
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
          }
        }
      )
    );
  }

  /**
   * Resuelve el modo de apertura del detalle de la persona.
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
    return this.authService.hasAuthority('ESB-PER-E');
  }

  private hasViewAuthority(): boolean {
    return this.authService.hasAuthority('ESB-PER-V');
  }

}
