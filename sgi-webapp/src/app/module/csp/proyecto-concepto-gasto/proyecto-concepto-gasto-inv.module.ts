import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoConceptoGastoDataResolver } from './proyecto-concepto-gasto-data.resolver';
import { ProyectoConceptoGastoRoutingInv } from './proyecto-concepto-gasto-routing-inv.module';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoConceptoGastoRoutingInv,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ],
  providers: [
    ProyectoConceptoGastoDataResolver
  ]
})
export class ProyectoConceptoGastoInvModule { }
