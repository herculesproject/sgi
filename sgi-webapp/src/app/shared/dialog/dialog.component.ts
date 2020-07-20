import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent {

  message: string;
  cancelButtonText = '';
  continuarButtonText = '';

  constructor(
    protected logger: NGXLogger,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private dialogRef: MatDialogRef<DialogComponent>
  ) {
    this.logger.debug(DialogComponent.name, 'constructor(protected logger: NGXLogger, private dialogRef: MatDialogRef<DialogComponent>');
    if (data) {
      this.message = data.message || this.message;
      if (data.cancel) {
        this.cancelButtonText = data.cancel || this.cancelButtonText;
      }
      if (data.ok) {
        this.continuarButtonText = data.ok || this.continuarButtonText;
      }

    }
    this.logger.debug(DialogComponent.name, 'constructor(protected logger: NGXLogger, private dialogRef: MatDialogRef<DialogComponent>');
  }

  /**
   * Aceptar mensaje de confirmacion
   */
  confirmacionClick(): void {
    this.logger.debug(DialogComponent.name, 'confirmacionClick()', 'start');
    this.dialogRef.close(true);
    this.logger.debug(DialogComponent.name, 'confirmacionClick()', 'end');
  }

}

