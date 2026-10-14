import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { UnidadVinculacionSelected } from '../unidad-vinculacion-tree.component';

export interface SearchUnidadVinculacionModalData {
  selectedUnidadesVinculacionRefs?: string[];
  onlyActive?: boolean;
}

@Component({
  templateUrl: './search-unidad-vinculacion.component.html',
  styleUrls: ['./search-unidad-vinculacion.component.scss']
})
export class SearchUnidadVinculacionModalComponent extends DialogFormComponent<IUnidadVinculacion> {

  private selectedValue: UnidadVinculacionSelected | null = null;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SearchUnidadVinculacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchUnidadVinculacionModalData
  ) {
    super(matDialogRef, false);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      unidadVinculacion: new FormControl(null, Validators.required)
    });
  }

  protected getValue(): IUnidadVinculacion {
    return this.selectedValue?.unidad ?? null;
  }

  onSelectionChange(value: UnidadVinculacionSelected | null): void {
    this.selectedValue = value;
    this.formGroup.controls.unidadVinculacion.setValue(value?.unidad ?? null);
  }
}
