import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ITipoHito } from '@core/models/csp/tipo-hito';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { NGXLogger } from 'ngx-logger';

const MSG_ERROR_FORM_GROUP = marker('form-group.error');

@Component({
  selector: 'sgi-tipo-hito-modal',
  templateUrl: './tipo-hito-modal.component.html',
  styleUrls: ['./tipo-hito-modal.component.scss']
})
export class TipoHitoModalComponent implements OnInit {
  formGroup: FormGroup;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<TipoHitoModalComponent>,
    @Inject(MAT_DIALOG_DATA) public tipoHito: ITipoHito
  ) {
    this.logger.debug(TipoHitoModalComponent.name, 'constructor()', 'start');
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';
    this.logger.debug(TipoHitoModalComponent.name, 'constructor()', 'end');
  }

  ngOnInit(): void {
    this.logger.debug(TipoHitoModalComponent.name, 'ngOnInit()', 'start');
    this.formGroup = new FormGroup({
      nombre: new FormControl(this.tipoHito?.nombre),
      descripcion: new FormControl(this.tipoHito?.descripcion)
    });
    if (this.tipoHito?.id) {
      FormGroupUtil.addFormControl(this.formGroup, 'activo', new FormControl('' + this.tipoHito.activo));
    }
    this.logger.debug(TipoHitoModalComponent.name, 'ngOnInit()', 'end');
  }

  closeModal(entidadFinanciadora?: ITipoHito): void {
    this.logger.debug(TipoHitoModalComponent.name, `${this.closeModal.name}()`, 'start');
    this.matDialogRef.close(entidadFinanciadora);
    this.logger.debug(TipoHitoModalComponent.name, `${this.closeModal.name}()`, 'end');
  }

  saveOrUpdate(): void {
    this.logger.debug(TipoHitoModalComponent.name, `${this.saveOrUpdate.name}()`, 'start');
    if (FormGroupUtil.valid(this.formGroup)) {
      this.loadDatosForm();
      this.closeModal(this.tipoHito);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
    this.logger.debug(TipoHitoModalComponent.name, `${this.saveOrUpdate.name}()`, 'end');
  }

  /**
   * Método para actualizar la entidad con los datos de un formGroup
   */
  private loadDatosForm(): void {
    this.logger.debug(TipoHitoModalComponent.name, `${this.loadDatosForm.name}()`, 'start');
    this.tipoHito.nombre = FormGroupUtil.getValue(this.formGroup, 'nombre');
    this.tipoHito.descripcion = FormGroupUtil.getValue(this.formGroup, 'descripcion');
    if (this.tipoHito?.id) {
      this.tipoHito.activo = FormGroupUtil.getValue(this.formGroup, 'activo');
    }
    this.logger.debug(TipoHitoModalComponent.name, `${this.loadDatosForm.name}()`, 'end');
  }

}
