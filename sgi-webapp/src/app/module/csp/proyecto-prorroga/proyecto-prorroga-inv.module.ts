import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoProrrogaDataResolver } from './proyecto-prorroga-data.resolver';
import { ProyectoProrrogaRoutingInv } from './proyecto-prorroga-routing-inv.module';

@NgModule({
  declarations: [],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoProrrogaRoutingInv,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ],
  providers: [
    ProyectoProrrogaDataResolver
  ]
})
export class ProyectoProrrogaInvModule { }
