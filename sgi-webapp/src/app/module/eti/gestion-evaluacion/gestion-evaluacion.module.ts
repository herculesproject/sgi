import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GestionEvaluacionListadoComponent } from './gestion-evaluacion-listado/gestion-evaluacion-listado.component';
import { GestionEvaluacionRoutingModule } from './gestion-evaluacion-routing.module';

@NgModule({
  declarations: [
    GestionEvaluacionListadoComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    GestionEvaluacionRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class GestionEvaluacionModule { }
