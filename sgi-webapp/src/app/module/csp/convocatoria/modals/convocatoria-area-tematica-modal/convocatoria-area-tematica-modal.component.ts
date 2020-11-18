import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IAreaTematicaArbol } from '@core/models/csp/area-tematica-arbol';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

@Component({
  templateUrl: './convocatoria-area-tematica-modal.component.html',
  styleUrls: ['./convocatoria-area-tematica-modal.component.scss']
})
export class ConvocatoriaAreaTematicaModalComponent extends
  BaseModalComponent<IConvocatoriaAreaTematica, ConvocatoriaAreaTematicaModalComponent> implements OnInit {
  areaTematicaArbol$: Observable<IAreaTematicaArbol[]>;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaAreaTematicaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IConvocatoriaAreaTematica
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, 'constructor()', 'start');
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, 'ngOnInit()', 'start');
    super.ngOnInit();
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, 'ngOnInit()', 'end');
  }

  protected getDatosForm(): IConvocatoriaAreaTematica {
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, `${this.getDatosForm.name}()`, 'start');
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, `${this.getDatosForm.name}()`, 'end');
    return null;
  }
  protected getFormGroup(): FormGroup {
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, `${this.getFormGroup.name}()`, 'start');
    this.logger.debug(ConvocatoriaAreaTematicaModalComponent.name, `${this.getFormGroup.name}()`, 'end');
    return null;
  }

}
