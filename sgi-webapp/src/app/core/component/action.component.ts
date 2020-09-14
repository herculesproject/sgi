import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IActionService } from '@core/services/action-service';
import { Observable, of } from 'rxjs';
import { DialogService } from '@core/services/dialog.service';
import { map } from 'rxjs/operators';

const MSG_FORM_UNSAVED = marker('common.dialog.unsaved.msg');
const MSG_FORM_UNSAVED_CANCEL = marker('botones.cancelar');
const MSG_FORM_UNSAVED_CONTINUE = marker('common.dialog.unsaved.button.continue');

export interface SgiAllowNavigation {
  allowNavigation(): Observable<boolean>;
}

export abstract class ActionComponent implements SgiAllowNavigation {
  // tslint:disable-next-line: variable-name
  private _service: IActionService;


  constructor(actionService: IActionService, private dialogService: DialogService) {
    this._service = actionService;
  }

  allowNavigation(): Observable<boolean> {
    if (this._service.hasChanges()) {
      return this.dialogService.showConfirmation(
        MSG_FORM_UNSAVED,
        {},
        MSG_FORM_UNSAVED_CONTINUE,
        MSG_FORM_UNSAVED_CANCEL
      ).pipe(map((val) => val));
    }
    return of(true);
  }

  abstract saveOrUpdate(): void;


}
