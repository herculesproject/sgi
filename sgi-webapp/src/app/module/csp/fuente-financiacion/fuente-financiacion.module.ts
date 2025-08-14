import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { FuenteFinanciacionListadoComponent } from './fuente-financiacion-listado/fuente-financiacion-listado.component';
import { FuenteFinanciacionModalComponent } from './fuente-financiacion-modal/fuente-financiacion-modal.component';
import { FuenteFinanciacionRoutingModule } from './fuente-financiacion-routing.module';

@NgModule({
  declarations: [
    FuenteFinanciacionListadoComponent,
    FuenteFinanciacionModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FuenteFinanciacionRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ]
})
export class FuenteFinanciacionModule { }
