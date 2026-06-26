import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoSocioPeriodoJustificacionDataResolver } from './proyecto-socio-periodo-justificacion-data.resolver';
import { ProyectoSocioPeriodoJustificacionRoutingInv } from './proyecto-socio-periodo-justificacion-routing-inv.module';

@NgModule({
  declarations: [],
  imports: [
    CspSharedModule,
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoSocioPeriodoJustificacionRoutingInv,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ProyectoSocioPeriodoJustificacionDataResolver
  ]
})
export class ProyectoSocioPeriodoJustificacionModuleInv { }
