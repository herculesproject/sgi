import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISeguimientoCientifico } from '@core/models/csp/seguimiento-cientifico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './convocatoria-seguimiento-cientifico-modal.component.html',
  styleUrls: ['./convocatoria-seguimiento-cientifico-modal.component.scss']
})
export class ConvocatoriaSeguimientoCientificoModalComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties2: FxFlexProperties;
  fxFlexProperties: FxFlexProperties;
  suscripciones: Subscription[];

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaSeguimientoCientificoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public seguimientoCientifico: ISeguimientoCientifico,
  ) {
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'constructor()', 'start');

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(20%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties2 = new FxFlexProperties();
    this.fxFlexProperties2.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties2.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'ngOnInit()', 'start');
    this.suscripciones = [];
    this.formGroup = new FormGroup({
      numPeriodo: new FormControl(this.seguimientoCientifico?.numPeriodo),
      desdeMes: new FormControl(this.seguimientoCientifico?.mesInicial),
      hastaMes: new FormControl(this.seguimientoCientifico?.mesFinal),
      fechaInicio: new FormControl(this.seguimientoCientifico?.fechaInicio),
      fechaFin: new FormControl(this.seguimientoCientifico?.fechaFin),
      observaciones: new FormControl(this.seguimientoCientifico?.observaciones)
    });
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'ngOnInit()', 'start');
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Cierra la ventana modal
   */
  closeModal(seguimientoCientifico?: ISeguimientoCientifico): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(seguimientoCientifico);
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'closeModal()', 'end');
  }

  /**
   * Actualizar o guardar datos
   */
  saveOrUpdate(): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'saveOrUpdate()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.seguimientoCientifico);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'saveOrUpdate()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'loadDatosForm()', 'start');
    this.seguimientoCientifico.numPeriodo = FormGroupUtil.getValue(this.formGroup, 'numPeriodo');
    this.seguimientoCientifico.mesInicial = FormGroupUtil.getValue(this.formGroup, 'desdeMes');
    this.seguimientoCientifico.mesFinal = FormGroupUtil.getValue(this.formGroup, 'hastaMes');
    this.seguimientoCientifico.fechaInicio = FormGroupUtil.getValue(this.formGroup, 'fechaInicio');
    this.seguimientoCientifico.fechaFin = FormGroupUtil.getValue(this.formGroup, 'fechaFin');
    this.seguimientoCientifico.observaciones = FormGroupUtil.getValue(this.formGroup, 'observaciones');
    this.logger.debug(ConvocatoriaSeguimientoCientificoModalComponent.name, 'loadDatosForm()', 'end');
  }
}
