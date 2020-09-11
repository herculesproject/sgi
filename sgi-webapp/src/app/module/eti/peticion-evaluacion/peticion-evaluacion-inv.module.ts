import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SgiAuthModule } from '@sgi/framework/auth';
import { PeticionEvaluacionListadoInvComponent } from './peticion-evaluacion-listado-inv/peticion-evaluacion-listado-inv.component';
import { PeticionEvaluacionInvRoutingModule } from './peticion-evaluacion-inv-routing.module';

@NgModule({
  declarations: [
    PeticionEvaluacionListadoInvComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    PeticionEvaluacionInvRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule
  ]
})
export class PeticionEvaluacionInvModule { }
