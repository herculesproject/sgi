import { Component, OnInit, Inject } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { FormGroup, FormControl, Validators } from '@angular/forms';

const MSG_ANADIR = marker('botones.aniadir');
const MSG_ACEPTAR = marker('botones.aceptar');

export interface ProyectoSocioPeriodoPagoModalData {
  proyectoSocioPeriodoPago: IProyectoSocioPeriodoPago;
  selectedFechaPrevistas: Date[];
  isEdit: boolean;
}

@Component({
  templateUrl: './proyecto-socio-periodo-pago-modal.component.html',
  styleUrls: ['./proyecto-socio-periodo-pago-modal.component.scss']
})
export class ProyectoSocioPeriodoPagoModalComponent extends
  BaseModalComponent<ProyectoSocioPeriodoPagoModalData, ProyectoSocioPeriodoPagoModalComponent>
  implements OnInit {
  textSaveOrUpdate: string;

  constructor(
    protected logger: NGXLogger,
    protected snackBarService: SnackBarService,
    public matDialogRef: MatDialogRef<ProyectoSocioPeriodoPagoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoSocioPeriodoPagoModalData,
  ) {
    super(logger, snackBarService, matDialogRef, data);
    this.logger.debug(ProyectoSocioPeriodoPagoModalComponent.name, 'constructor()', 'start');
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
    this.logger.debug(ProyectoSocioPeriodoPagoModalComponent.name, 'constructor()', 'end');
  }

  protected getDatosForm(): ProyectoSocioPeriodoPagoModalData {
    this.logger.debug(ProyectoSocioPeriodoPagoModalComponent.name, `getDatosForm()`, 'start');
    this.data.proyectoSocioPeriodoPago.numPeriodo = this.formGroup.get('numPeriodo').value;
    this.data.proyectoSocioPeriodoPago.fechaPrevistaPago = this.formGroup.get('fechaPrevistaPago').value;
    this.data.proyectoSocioPeriodoPago.importe = this.formGroup.get('importe').value;
    this.data.proyectoSocioPeriodoPago.fechaPago = this.formGroup.get('fechaPago').value;
    this.logger.debug(ProyectoSocioPeriodoPagoModalComponent.name, `getDatosForm()`, 'end');
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    this.logger.debug(ProyectoSocioPeriodoPagoModalComponent.name, `getFormGroup()`, 'start');
    const formGroup = new FormGroup(
      {
        numPeriodo: new FormControl({
          value: this.data.proyectoSocioPeriodoPago.numPeriodo,
          disabled: true
        },
          [Validators.required]
        ),
        fechaPrevistaPago: new FormControl(
          this.data.proyectoSocioPeriodoPago.fechaPrevistaPago, [Validators.required]
        ),
        importe: new FormControl(
          this.data.proyectoSocioPeriodoPago.importe,
          [
            Validators.required,
            Validators.min(1),
            Validators.max(2_147_483_647)
          ]
        ),
        fechaPago: new FormControl(this.data.proyectoSocioPeriodoPago.fechaPago, []),
      }
    );
    this.logger.debug(ProyectoSocioPeriodoPagoModalComponent.name, `getFormGroup()`, 'end');
    return formGroup;
  }
}
