import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgoFormlyFormsModule } from '../formly-forms/sgo-formly-forms.module';
import { AreaConocimientoModalComponent } from './area-conocimiento-modal/area-conocimiento-modal.component';
import { ClasificacionModalComponent } from './clasificacion-modal/clasificacion-modal.component';
import { SelectComunidadAutonomaComponent } from './select-comunidad-autonoma/select-comunidad-autonoma.component';
import { SelectPaisComponent } from './select-pais/select-pais.component';
import { SelecProvinciaComponent } from './select-provincia/select-provincia.component';
import { SearchUnidadVinculacionModalComponent } from './select-unidad-vinculacion/dialog/search-unidad-vinculacion.component';
import { SelectUnidadVinculacionComponent } from './select-unidad-vinculacion/select-unidad-vinculacion.component';
import { UnidadVinculacionTreeComponent } from './select-unidad-vinculacion/unidad-vinculacion-tree.component';

@NgModule({
  declarations: [
    AreaConocimientoModalComponent,
    ClasificacionModalComponent,
    SearchUnidadVinculacionModalComponent,
    SelecProvinciaComponent,
    SelectComunidadAutonomaComponent,
    SelectPaisComponent,
    SelectUnidadVinculacionComponent,
    UnidadVinculacionTreeComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgoFormlyFormsModule
  ],
  exports: [
    AreaConocimientoModalComponent,
    ClasificacionModalComponent,
    SelecProvinciaComponent,
    SelectComunidadAutonomaComponent,
    SelectPaisComponent,
    SelectUnidadVinculacionComponent,
    UnidadVinculacionTreeComponent
  ]
})
export class SgoSharedModule { }
