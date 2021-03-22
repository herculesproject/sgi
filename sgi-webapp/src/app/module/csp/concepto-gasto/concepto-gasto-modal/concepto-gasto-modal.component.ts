import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConceptoGasto } from '@core/models/csp/tipos-configuracion';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const MSG_ERROR_FORM_GROUP = marker('error.form-group');
const CONCEPTO_GASTO_KEY = marker('csp.concepto-gasto');
const CONCEPTO_GASTO_NOMBRE_KEY = marker('csp.concepto-gasto.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  selector: 'sgi-concepto-gasto-modal',
  templateUrl: './concepto-gasto-modal.component.html',
  styleUrls: ['./concepto-gasto-modal.component.scss']
})
export class ConceptoGastoModalComponent extends
  BaseModalComponent<IConceptoGasto, ConceptoGastoModalComponent> implements OnInit {

  msgParamNombreEntity = {};
  title: string;

  constructor(
    protected readonly snackBarService: SnackBarService,
    public readonly matDialogRef: MatDialogRef<ConceptoGastoModalComponent>,
    @Inject(MAT_DIALOG_DATA)
    public conceptoGasto: IConceptoGasto,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, conceptoGasto);
    if (conceptoGasto) {
      this.conceptoGasto = { ...conceptoGasto };
    } else {
      this.conceptoGasto = { activo: true } as IConceptoGasto;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      nombre: new FormControl(this.conceptoGasto?.nombre),
      descripcion: new FormControl(this.conceptoGasto?.descripcion),
    });
  }

  private setupI18N(): void {
    this.translate.get(
      CONCEPTO_GASTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    if (this.conceptoGasto.nombre) {
      this.translate.get(
        CONCEPTO_GASTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONCEPTO_GASTO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);

    }

  }

  closeModal(conceptoGasto?: IConceptoGasto): void {
    this.matDialogRef.close(conceptoGasto);
  }

  saveOrUpdate(): void {
    if (FormGroupUtil.valid(this.formGroup)) {
      this.getDatosForm();
      this.closeModal(this.conceptoGasto);
    } else {
      this.snackBarService.showError(MSG_ERROR_FORM_GROUP);
    }
  }

  protected getDatosForm(): IConceptoGasto {
    const conceptoGasto = this.conceptoGasto;
    conceptoGasto.nombre = this.formGroup.get('nombre').value;
    conceptoGasto.descripcion = this.formGroup.get('descripcion').value;
    return conceptoGasto;
  }

  protected getFormGroup(): FormGroup {
    return new FormGroup({
      nombre: new FormControl(this.conceptoGasto?.nombre),
      descripcion: new FormControl(this.conceptoGasto?.descripcion)
    });
  }

}
