import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EvaluacionFormularioModule } from '../evaluacion-formulario/evaluacion-formulario.module';
import { EvaluacionListadoAnteriorMemoriaComponent } from '../evaluacion-formulario/evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionResolver } from '../evaluacion/evaluacion.resolver';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { EvaluacionEvaluarComponent } from './evaluacion-evaluar/evaluacion-evaluar.component';
import { EvaluacionListadoComponent } from './evaluacion-listado/evaluacion-listado.component';
import { EvaluacionRoutingModule } from './evaluacion-routing.module';


@NgModule({
  declarations: [
    EvaluacionListadoComponent,
    EvaluacionEvaluarComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    EvaluacionRoutingModule,
    EvaluacionFormularioModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    SgpSharedModule,
    EtiSharedModule
  ],
  exports: [
    EvaluacionListadoAnteriorMemoriaComponent,
  ],
  providers: [
    EvaluacionResolver
  ]
})
export class EvaluacionModule { }
