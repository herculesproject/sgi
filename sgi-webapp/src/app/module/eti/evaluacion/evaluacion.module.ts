import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { ComentarioModule } from '../comentario/comentario.module';
import { DocumentacionMemoriaModule } from '../documentacion-memoria/documentacion-memoria.module';
import { EvaluacionEvaluarComponent } from './evaluacion-evaluar/evaluacion-evaluar.component';
import {
  EvaluacionComentariosComponent,
} from './evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.component';
import {
  EvaluacionDatosMemoriaComponent,
} from './evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.component';
import {
  EvaluacionDocumentacionComponent,
} from './evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.component';
import {
  EvaluacionListadoAnteriorMemoriaComponent,
} from './evaluacion-formulario/evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { EvaluacionListadoComponent } from './evaluacion-listado/evaluacion-listado.component';
import { EvaluacionRoutingModule } from './evaluacion-routing.module';
import { EvaluacionResolver } from './evaluacion.resolver';

@NgModule({
  declarations: [
    EvaluacionEvaluarComponent,
    EvaluacionListadoComponent,
    EvaluacionComentariosComponent,
    EvaluacionDatosMemoriaComponent,
    EvaluacionDocumentacionComponent,
    EvaluacionListadoAnteriorMemoriaComponent,
    EvaluacionListadoComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    EvaluacionRoutingModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    ComentarioModule,
    DocumentacionMemoriaModule
  ],
  providers: [
    EvaluacionResolver
  ]
})
export class EvaluacionModule { }
