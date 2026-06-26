import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoSocioDataResolver } from './proyecto-socio-data.resolver';
import { ProyectoSocioRoutingInv } from './proyecto-socio-routing-inv.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    CspSharedModule,
    FormsModule,
    MaterialDesignModule,
    ProyectoSocioRoutingInv,
    ReactiveFormsModule,
    SgempSharedModule,
    SgiAuthModule,
    SharedModule,
    TranslateModule,
    SgpSharedModule
  ],
  providers: [
    ProyectoSocioDataResolver
  ]
})
export class ProyectoSocioInvModule { }
