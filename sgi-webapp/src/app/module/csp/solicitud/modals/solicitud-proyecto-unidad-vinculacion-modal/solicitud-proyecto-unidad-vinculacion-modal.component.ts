import { Component, Inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { UnidadVinculacionSelected } from '../../../shared/select-unidad-vinculacion/unidad-vinculacion-tree.component';
import {
  ISolicitudProyectoUnidadVinculacionListado
} from '../../solicitud-formulario/solicitud-proyecto-unidades-vinculacion/solicitud-proyecto-unidades-vinculacion.fragment';

export interface SolicitudProyectoUnidadVinculacionModalData {
  titleEntity: string;
  selectedUnidadesVinculacionRefs: string[];
  onlyActive?: boolean;
  solicitudProyectoUnidad?: ISolicitudProyectoUnidadVinculacionListado;
}

@Component({
  templateUrl: './solicitud-proyecto-unidad-vinculacion-modal.component.html',
  styleUrls: ['./solicitud-proyecto-unidad-vinculacion-modal.component.scss']
})
export class SolicitudProyectoUnidadVinculacionModalComponent
  extends DialogFormComponent<SolicitudProyectoUnidadVinculacionModalData> {

  private selectedValue: UnidadVinculacionSelected | null = null;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudProyectoUnidadVinculacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudProyectoUnidadVinculacionModalData
  ) {
    super(matDialogRef, false);
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      unidadVinculacion: new FormControl(null, Validators.required)
    });
  }

  protected getValue(): SolicitudProyectoUnidadVinculacionModalData {
    if (this.selectedValue) {
      this.data.solicitudProyectoUnidad = {
        id: undefined,
        solicitudProyectoId: undefined,
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
