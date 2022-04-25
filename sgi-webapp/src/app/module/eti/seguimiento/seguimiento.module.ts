import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { DocumentacionMemoriaModule } from '../documentacion-memoria/documentacion-memoria.module';
import { EvaluacionModule } from '../evaluacion/evaluacion.module';
import { SeguimientoFormularioModule } from '../seguimiento-formulario/seguimiento-formulario.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { SeguimientoEvaluarComponent } from './seguimiento-evaluar/seguimiento-evaluar.component';
import { SeguimientoListadoComponent } from './seguimiento-listado/seguimiento-listado.component';
import { SeguimientoRoutingModule } from './seguimiento-routing.module';
import { SeguimientoResolver } from './seguimiento.resolver';


@NgModule({
  declarations: [
    SeguimientoListadoComponent,
    SeguimientoEvaluarComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    SeguimientoRoutingModule,
    SeguimientoFormularioModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    DocumentacionMemoriaModule,
    EvaluacionModule,
    EtiSharedModule
  ],
  providers: [
    SeguimientoResolver,
  ]
})
export class SeguimientoModule { }
