import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { TipoOrigenFuenteFinanciacionListadoComponent } from './tipo-origen-fuente-financiacion-listado/tipo-origen-fuente-financiacion-listado.component';
import { TipoOrigenFuenteFinanciacionModalComponent } from './tipo-origen-fuente-financiacion-modal/tipo-origen-fuente-financiacion-modal.component';
import { TipoOrigenFuenteFinanciacionRoutingModule } from './tipo-origen-fuente-financiacion-rounting.module';

@NgModule({
  declarations: [TipoOrigenFuenteFinanciacionListadoComponent, TipoOrigenFuenteFinanciacionModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    TipoOrigenFuenteFinanciacionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoOrigenFuenteFinanciacionModule { }
