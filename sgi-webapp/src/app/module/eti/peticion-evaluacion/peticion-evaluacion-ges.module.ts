import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { PeticionEvaluacionGesRoutingModule } from './peticion-evaluacion-ges-routing.module';
import { PeticionEvaluacionListadoGesComponent } from './peticion-evaluacion-listado-ges/peticion-evaluacion-listado-ges.component';
import { PeticionEvaluacionResolver } from './peticion-evaluacion.resolver';


@NgModule({
  declarations: [
    PeticionEvaluacionListadoGesComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    PeticionEvaluacionGesRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SgpSharedModule,
    EtiSharedModule
  ],
  providers: [
    PeticionEvaluacionResolver
  ]
})
export class PeticionEvaluacionGesModule { }
