import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoPeriodoSeguimientoDataResolver } from './proyecto-periodo-seguimiento-data.resolver';
import { ProyectoPeriodoSeguimientoRoutingInv } from './proyecto-periodo-seguimiento-routing-inv.module';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoPeriodoSeguimientoRoutingInv,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ],
  providers: [
    ProyectoPeriodoSeguimientoDataResolver
  ]
})
export class ProyectoPeriodoSeguimientoInvModule { }
