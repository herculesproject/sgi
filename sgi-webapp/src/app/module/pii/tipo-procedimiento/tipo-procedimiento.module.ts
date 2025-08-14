import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoProcedimientoListadoComponent } from './tipo-procedimiento-listado/tipo-procedimiento-listado.component';
import { TipoProcedimientoModalComponent } from './tipo-procedimiento-modal/tipo-procedimiento-modal.component';
import { TipoProcedimientoRoutingModule } from './tipo-procedimiento-routing.module';

@NgModule({
  declarations: [
    TipoProcedimientoListadoComponent,
    TipoProcedimientoModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    TipoProcedimientoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoProcedimientoModule { }
