import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoFinanciacionListadoComponent } from './tipo-financiacion-listado/tipo-financiacion-listado.component';
import { TipoFinanciacionModalComponent } from './tipo-financiacion-modal/tipo-financiacion-modal.component';
import { TipoFinanciacionRoutingModule } from './tipo-financiacion-routing.module';

@NgModule({
  declarations: [
    TipoFinanciacionListadoComponent,
    TipoFinanciacionModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    TipoFinanciacionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
  ]
})
export class TipoFinanciacionModule { }
