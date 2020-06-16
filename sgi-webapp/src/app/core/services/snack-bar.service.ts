import { Injectable } from '@angular/core';
import { SnackBarComponent } from '@shared/componentes-shared/snack-bar/snack-bar.component';
import { NGXLogger } from 'ngx-logger';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackBarService {

  snackBarConfig = new MatSnackBarConfig();


  constructor(protected logger: NGXLogger, private snackBar: MatSnackBar) {
    this.logger.debug(SnackBarService.name, 'constructor(protected logger: NGXLogger, private snackBar: MatSnackBar)', 'start');

    this.snackBarConfig.duration = 2000;
    this.snackBarConfig.verticalPosition = 'top';

    this.logger.debug(SnackBarService.name, 'constructor(protected logger: NGXLogger, private snackBar: MatSnackBar)', 'end');
  }

  /**
   * Muestra un mensaje de error.
   *
   * @param mensaje el mensaje o lista de mensajes de error.
   */
  mostrarMensajeError(mensaje: string | string[]): void {
    if (!mensaje) {
      mensaje = 'Se ha producido un error';
    }

    this.snackBar.openFromComponent(SnackBarComponent, {
      ...this.snackBarConfig,
      data: mensaje,
      panelClass: 'error-snack-bar'
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
      panelClass: 'success-snack-bar'
    });
  }

}
