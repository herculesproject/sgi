import {Injectable} from '@angular/core';
import {MatSnackBar, MatSnackBarConfig} from '@angular/material/snack-bar';
import {SnackBarComponent} from '@shared/snack-bar/snack-bar.component';
import {NGXLogger} from 'ngx-logger';

import {TraductorService} from './traductor.service';

@Injectable({
  providedIn: 'root',
})
export class SnackBarService {
  snackBarConfig: MatSnackBarConfig;

  constructor(
    protected logger: NGXLogger,
    private snackBar: MatSnackBar,
    private readonly traductor: TraductorService
  ) {
    this.logger.debug(
      SnackBarService.name,
      'constructor(protected logger: NGXLogger, private snackBar: MatSnackBar)',
      'start'
    );
    this.snackBarConfig = new MatSnackBarConfig();
    this.snackBarConfig.duration = 2000;
    this.snackBarConfig.verticalPosition = 'top';

    this.logger.debug(
      SnackBarService.name,
      'constructor(protected logger: NGXLogger, private snackBar: MatSnackBar)',
      'end'
    );
  }

  /**
   * Muestra un mensaje de error.
   *
   * @param mensaje el mensaje o lista de mensajes de error.
   */
  mostrarMensajeError(mensaje: string | string[]): void {
    if (!mensaje) {
      mensaje = this.traductor.getTexto('snackBar.error');
    }

    this.snackBar.openFromComponent(SnackBarComponent, {
      ...this.snackBarConfig,
      data: mensaje,
      panelClass: 'error-snack-bar',
    });
  }

  /**
   * Muestra un mensaje de exito.
   *
   * @param mensaje el mensaje.
   */
  mostrarMensajeSuccess(mensaje: string): void {
    this.snackBar.openFromComponent(SnackBarComponent, {
      ...this.snackBarConfig,
      data: mensaje,
      panelClass: 'success-snack-bar',
    });
  }
}
