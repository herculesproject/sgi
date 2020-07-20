import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import { DialogComponent } from '@shared/dialog/dialog.component';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DialogService {
  private confirmado$: Observable<boolean>;

  constructor(protected logger: NGXLogger, private dialog: MatDialog) {
    this.logger.debug(DialogService.name, 'constructor(protected logger: NGXLogger, private dialog: MatDialog)', 'start');
    this.confirmado$ = new BehaviorSubject<boolean>(false);
    this.logger.debug(DialogService.name, 'constructor(protected logger: NGXLogger, private dialog: MatDialog)', 'end');
  }

  /**
   * Muestra un alert generico
   */
  dialogGenerico(mensaje: string | string[], buttonUno: string, buttonDos: string): void {
    this.logger.debug(DialogService.name, 'dialogGenerico(mensaje: string | string[], buttonUno: string, buttonDos: string', 'start');
    const dialogRef = this.dialog.open(DialogComponent, {
      ...this.dialog,
      data: {
        message: mensaje,
        ok: buttonUno,
        cancel: buttonDos
      },
      panelClass: 'confirmacion-dialog'
    });

    this.confirmado$ = dialogRef.afterClosed().pipe(
      map((confirmed: boolean) => confirmed ? confirmed : false)
    );

    this.logger.debug(DialogService.name, 'dialogGenerico(mensaje: string | string[], buttonUno: string, buttonDos: string', 'start');
  }

  /**
   * Devuelve el valor de la acción realizada
   */
  getAccionConfirmada(): Observable<boolean> {
    this.logger.debug(DialogService.name, 'getAccionConfirmada()', 'start');
    this.logger.debug(DialogService.name, 'getAccionConfirmada()', 'end');
    return this.confirmado$;
  }

}
