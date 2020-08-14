import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import { DialogComponent, DialogData } from '../../block/dialog/dialog.component';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(protected logger: NGXLogger, private dialog: MatDialog) {
    this.logger.debug(DialogService.name, 'constructor(protected logger: NGXLogger, private dialog: MatDialog)', 'start');
    this.logger.debug(DialogService.name, 'constructor(protected logger: NGXLogger, private dialog: MatDialog)', 'end');
  }

  showConfirmation(
    message: string,
    params: {} = {},
    ok: string = 'botones.acepter',
    cancel: string = 'botones.cancelar'
  ): Observable<boolean> {
    const dialogRef = this.dialog.open(DialogComponent, {
      ...this.dialog,
      data: {
        message,
        params,
        ok,
        cancel
      } as DialogData,
      panelClass: 'confirmacion-dialog'
    });

    return dialogRef.afterClosed().pipe(
      map((confirmed: boolean) => confirmed ? confirmed : false)
    );
  }
}
