import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './convocatoria-entidad-financiadora-modal.component.html',
  styleUrls: ['./convocatoria-entidad-financiadora-modal.component.scss']
})
export class ConvocatoriaEntidadFinanciadoraModalComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  suscripciones: Subscription[];

  fuentesFinanciacion: string[];
  tiposFinanciacion: string[];

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaEntidadFinanciadoraModalComponent>,
    @Inject(MAT_DIALOG_DATA) public entidadFinanciadora: IEntidadFinanciadora,
  ) {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.suscripciones = [];
    this.fuentesFinanciacion = [];
    this.tiposFinanciacion = [];
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'ngOnInit()', 'start');
    this.loadFuentesFinanciacion();
    this.loadTiposFinanciacion();
    this.formGroup = new FormGroup({
      nombre: new FormControl(this.entidadFinanciadora?.nombre),
      fuenteFinanciacion: new FormControl(this.entidadFinanciadora?.fuenteFinanciacion),
      tipoFinanciacion: new FormControl(this.entidadFinanciadora?.tipoFinanciacion),
      porcentajeFinanciacion: new FormControl(this.entidadFinanciadora?.porcentajeFinanciacion)
    });
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Carga todas las fuentes de financiación
   */
  loadFuentesFinanciacion(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'loadFuentesFinanciacion()', 'start');
    // TODO debería llamarse a un servicio para obterner los datos
    this.fuentesFinanciacion = ['PGE', 'Fondos propios 2020', 'Otros'];
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'loadFuentesFinanciacion()', 'end');
  }

  /**
   * Carga todos los tipos de financiación
   */
  loadTiposFinanciacion(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'loadFuentesFinanciacion()', 'start');
    // TODO debería llamarse a un servicio para obterner los datos
    this.tiposFinanciacion = ['Subvención', 'Otros'];
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'loadFuentesFinanciacion()', 'end');
  }

  getNombreFuenteFinanciacion(fuenteFinanciacion: string) {
    // TODO debería llegar una interfaz
    return fuenteFinanciacion;
  }

  getNombreTipoFinanciacion(tipoFinanciacion: string) {
    // TODO debería llegar una interfaz
    return tipoFinanciacion;
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Cierra la ventana modal y devuelve la entidad financiadora
   *
   * @param entidadFinanciadora Entidad financiadora modificada
   */
  closeModal(entidadFinanciadora?: IEntidadFinanciadora): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(entidadFinanciadora);
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'closeModal()', 'end');
  }

  saveOrUpdate(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'updateComentario()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.entidadFinanciadora);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'updateComentario()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'getDatosForm()', 'start');
    this.entidadFinanciadora.nombre = FormGroupUtil.getValue(this.formGroup, 'nombre');
    this.entidadFinanciadora.fuenteFinanciacion = FormGroupUtil.getValue(this.formGroup, 'fuenteFinanciacion');
    this.entidadFinanciadora.tipoFinanciacion = FormGroupUtil.getValue(this.formGroup, 'tipoFinanciacion');
    this.entidadFinanciadora.porcentajeFinanciacion = FormGroupUtil.getValue(this.formGroup, 'porcentajeFinanciacion');
    this.logger.debug(ConvocatoriaEntidadFinanciadoraModalComponent.name, 'getDatosForm()', 'end');
  }
}
