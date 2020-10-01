import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEnlace } from '@core/models/csp/enlace';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  templateUrl: './convocatoria-enlace-modal.component.html',
  styleUrls: ['./convocatoria-enlace-modal.component.scss']
})
export class ConvocatoriaEnlaceModalComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  suscripciones: Subscription[];
  tiposEnlaces: string[];

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConvocatoriaEnlaceModalComponent>,
    @Inject(MAT_DIALOG_DATA) public enlace: IEnlace,
  ) {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnInit()', 'start');
    this.suscripciones = [];
    this.tiposEnlaces = [];
    this.loadTiposEnlaces();
    this.formGroup = new FormGroup({
      url: new FormControl(this.enlace?.url),
      descripcion: new FormControl(this.enlace?.descripcion),
      tipoEnlace: new FormControl(this.enlace?.tipoEnlace),
    });
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnInit()', 'end');
  }

  /**
   * Carga todos los tipos de financiación
   */
  loadTiposEnlaces(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'loadFuentesFinanciacion()', 'start');
    // TODO debería llamarse a un servicio para obterner los datos
    this.tiposEnlaces = ['BOE', 'Publicación convocatoria'];
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'loadFuentesFinanciacion()', 'end');
  }

  getNombreTipoEnlace(tipoFinanciacion: string) {
    // TODO debería llegar una interfaz
    return tipoFinanciacion;
  }

  ngOnDestroy(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnDestroy()', 'start');
    this.suscripciones?.forEach(x => x.unsubscribe());
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'ngOnDestroy()', 'end');
  }

  /**
   * Cierra la ventana modal y devuelve la entidad financiadora
   *
   * @param enlace Entidad financiadora modificada
   */
  closeModal(enlace?: IEnlace): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'closeModal()', 'start');
    this.matDialogRef.close(enlace);
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'closeModal()', 'end');
  }

  saveOrUpdate(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'updateComentario()', 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.enlace);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'updateComentario()', 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   *
   * @returns Comentario con los datos del formulario
   */
  private loadDatosForm(): void {
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getDatosForm()', 'start');
    this.enlace.url = FormGroupUtil.getValue(this.formGroup, 'url');
    this.enlace.descripcion = FormGroupUtil.getValue(this.formGroup, 'descripcion');
    this.enlace.tipoEnlace = FormGroupUtil.getValue(this.formGroup, 'tipoEnlace');
    this.logger.debug(ConvocatoriaEnlaceModalComponent.name, 'getDatosForm()', 'end');
  }

}
