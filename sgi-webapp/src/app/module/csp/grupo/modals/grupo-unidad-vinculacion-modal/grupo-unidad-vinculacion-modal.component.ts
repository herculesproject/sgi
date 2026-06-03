import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { UnidadVinculacionSelected } from '../../../shared/select-unidad-vinculacion/unidad-vinculacion-tree.component';
import { IGrupoUnidadVinculacionListado } from '../../grupo-formulario/grupo-unidades-vinculacion/grupo-unidades-vinculacion.fragment';

export interface GrupoUnidadVinculacionModalData {
  titleEntity: string;
  selectedUnidadesVinculacionRefs: string[];
  onlyActive?: boolean;
  grupoUnidad?: IGrupoUnidadVinculacionListado;
}

@Component({
  templateUrl: './grupo-unidad-vinculacion-modal.component.html',
  styleUrls: ['./grupo-unidad-vinculacion-modal.component.scss']
})
export class GrupoUnidadVinculacionModalComponent extends DialogFormComponent<GrupoUnidadVinculacionModalData> {

  private selectedValue: UnidadVinculacionSelected | null = null;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<GrupoUnidadVinculacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GrupoUnidadVinculacionModalData
  ) {
    super(matDialogRef, false);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      unidadVinculacion: new FormControl(null, Validators.required)
    });
  }

  protected getValue(): GrupoUnidadVinculacionModalData {
    if (this.selectedValue) {
      this.data.grupoUnidad = {
        id: undefined,
        grupoId: undefined,
        unidadVinculacion: this.selectedValue.unidad,
        tipoUnidad: this.selectedValue.tipo
      };
    }
    return this.data;
  }

  onSelectionChange(value: UnidadVinculacionSelected | null): void {
    this.selectedValue = value;
    this.formGroup.controls.unidadVinculacion.setValue(value?.unidad ?? null);
  }
}
