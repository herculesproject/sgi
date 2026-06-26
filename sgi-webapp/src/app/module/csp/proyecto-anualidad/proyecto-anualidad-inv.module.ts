import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule } from '@herculesproject/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoAnualidadDataResolver } from './proyecto-anualidad-data.resolver';
import { ProyectoAnualidadRoutingInv } from './proyecto-anualidad-routing-inv.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    CspSharedModule,
    FormsModule,
    MaterialDesignModule,
    ProyectoAnualidadRoutingInv,
    ReactiveFormsModule,
    SgiAuthModule,
    SharedModule,
    TranslateModule
  ],
  providers: [
    ProyectoAnualidadDataResolver
  ]
})
export class ProyectoAnualidadInvModule { }
