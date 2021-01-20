import { Component, Inject, OnInit } from '@angular/core';
import { ISolicitudProyectoPeriodoPago } from '@core/models/csp/solicitud-proyecto-periodo-pago';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { FormGroup, Validators, FormControl } from '@angular/forms';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { tap } from 'rxjs/operators';
import { RangeValidator } from '@core/validators/range-validator';

const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

export interface SolicitudProyectoSocioPeriodoPagoModalData {
  solicitudProyectoPeriodoPago: ISolicitudProyectoPeriodoPago;
  selectedMeses: number[];
  isEdit: boolean;
}

@Component({
  templateUrl: './solicitud-proyecto-socio-periodo-pago-modal.component.html',
  styleUrls: ['./solicitud-proyecto-socio-periodo-pago-modal.component.scss']
})
export class SolicitudProyectoSocioPeriodoPagoModalComponent extends
  BaseModalComponent<SolicitudProyectoSocioPeriodoPagoModalData, SolicitudProyectoSocioPeriodoPagoModalComponent>
  implements OnInit {
  textSaveOrUpdate: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<SolicitudProyectoSocioPeriodoPagoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoSocioPeriodoPagoModalData,
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoModalComponent.name, 'constructor()', 'start');
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'row';
    this.textSaveOrUpdate = this.data.isEdit ? MSG_ACEPTAR : MSG_ANADIR;
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoModalComponent.name, 'constructor()', 'end');
  }

  protected getDatosForm(): SolicitudProyectoSocioPeriodoPagoModalData {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoModalComponent.name, `getDatosForm()`, 'start');
    this.data.solicitudProyectoPeriodoPago.numPeriodo = this.formGroup.get('numPeriodo').value;
    this.data.solicitudProyectoPeriodoPago.mes = this.formGroup.get('mes').value;
    this.data.solicitudProyectoPeriodoPago.importe = this.formGroup.get('importe').value;
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoModalComponent.name, `getDatosForm()`, 'end');
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup(
      {
        numPeriodo: new FormControl({
          value: this.data.solicitudProyectoPeriodoPago.numPeriodo,
          disabled: true
        },
          [Validators.required]
        ),
        mes: new FormControl(
          this.data.solicitudProyectoPeriodoPago.mes,
          [
            Validators.min(1),
            Validators.max(9999),
            Validators.required,
            RangeValidator.contains(this.data.selectedMeses)
          ]
        ),
        importe: new FormControl(
          this.data.solicitudProyectoPeriodoPago.importe,
          [Validators.min(1), Validators.max(2_147_483_647)]
        ),
      }
    );
    this.logger.debug(SolicitudProyectoSocioPeriodoPagoModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }
}
