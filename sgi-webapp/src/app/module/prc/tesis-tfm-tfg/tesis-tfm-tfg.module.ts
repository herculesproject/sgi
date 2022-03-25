import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { TesisTfmTfgRoutingModule } from './tesis-tfm-tfg-routing.module';
import { TesisTfmTfgListadoComponent } from './tesis-tfm-tfg-listado/tesis-tfm-tfg-listado.component';

@NgModule({
  declarations: [TesisTfmTfgListadoComponent],
  imports: [
    CommonModule,
    SharedModule,
    TesisTfmTfgRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    SgpSharedModule,
    CspSharedModule,
  ]
})
export class TesisTfmTfgModule { }
