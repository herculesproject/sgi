import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { UnidadVinculacionSelected } from '../../../shared/select-unidad-vinculacion/unidad-vinculacion-tree.component';
import { IProyectoUnidadVinculacionListado } from '../../proyecto-formulario/proyecto-unidades-vinculacion/proyecto-unidades-vinculacion.fragment';

export interface ProyectoUnidadVinculacionModalData {
  titleEntity: string;
  selectedUnidadesVinculacionRefs: string[];
  onlyActive?: boolean;
  proyectoUnidad?: IProyectoUnidadVinculacionListado;
}

@Component({
  templateUrl: './proyecto-unidad-vinculacion-modal.component.html',
  styleUrls: ['./proyecto-unidad-vinculacion-modal.component.scss']
})
export class ProyectoUnidadVinculacionModalComponent extends DialogFormComponent<ProyectoUnidadVinculacionModalData> {

  private selectedValue: UnidadVinculacionSelected | null = null;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<ProyectoUnidadVinculacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoUnidadVinculacionModalData
  ) {
    super(matDialogRef, false);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      unidadVinculacion: new FormControl(null, Validators.required)
    });
  }

  protected getValue(): ProyectoUnidadVinculacionModalData {
    if (this.selectedValue) {
      this.data.proyectoUnidad = {
        id: undefined,
        proyectoId: undefined,
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
