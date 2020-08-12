import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';

import { ComponentsModule } from '../../components.module';
import { EvaluacionListadoComponent } from './evaluacion-listado/evaluacion-listado.component';
import { EvaluacionRoutingModule } from './evaluacion-routing.module';

@NgModule({
  declarations: [
    EvaluacionListadoComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ComponentsModule,
    PerfectScrollbarModule,
    EvaluacionRoutingModule,
    SgiAuthModule
  ]
})
export class EvaluacionModule { }
