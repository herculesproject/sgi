import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { UnidadVinculacionSelected } from 'src/app/esb/sgo/shared/select-unidad-vinculacion/unidad-vinculacion-tree.component';
import {
  IPeticionEvaluacionUnidadVinculacionListado
} from '../../peticion-evaluacion-formulario/peticion-evaluacion-unidades-vinculacion/peticion-evaluacion-unidades-vinculacion.fragment';

export interface PeticionEvaluacionUnidadVinculacionModalData {
  titleEntity: string;
  selectedUnidadesVinculacionRefs: string[];
  onlyActive?: boolean;
  unidadVinculacion?: IPeticionEvaluacionUnidadVinculacionListado;
}

@Component({
  templateUrl: './peticion-evaluacion-unidad-vinculacion-modal.component.html',
  styleUrls: ['./peticion-evaluacion-unidad-vinculacion-modal.component.scss']
})
export class PeticionEvaluacionUnidadVinculacionModalComponent
  extends DialogFormComponent<PeticionEvaluacionUnidadVinculacionModalData> {

  private selectedValue: UnidadVinculacionSelected | null = null;

  get MSG_PARAMS(): typeof MSG_PARAMS {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<PeticionEvaluacionUnidadVinculacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PeticionEvaluacionUnidadVinculacionModalData
  ) {
    super(matDialogRef, false);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      unidadVinculacion: new FormControl(null, Validators.required)
    });
  }

  protected getValue(): PeticionEvaluacionUnidadVinculacionModalData {
    if (this.selectedValue) {
      this.data.unidadVinculacion = {
        id: undefined,
        peticionEvaluacionId: undefined,
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
