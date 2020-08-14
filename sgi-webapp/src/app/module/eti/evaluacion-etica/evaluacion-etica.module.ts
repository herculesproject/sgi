import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { EvaluacionEticaRoutingModule } from './evaluacion-etica-routing.module';
import { EvaluacionEticaListadoComponent } from './evaluacion-etica-listado/evaluacion-etica-listado.component';
import { EvaluacionEticaCrearComponent } from './evaluacion-etica-crear/evaluacion-etica-crear.component';
import { EvaluacionEticaDatosGeneralesComponent } from './evaluacion-etica-crear/evaluacion-etica-datos-generales/evaluacion-etica-datos-generales.component';
import { EvaluacionEticaEquipoInvestigadorComponent } from './evaluacion-etica-crear/evaluacion-etica-equipo-investigador/evaluacion-etica-equipo-investigador.component';
import { EvaluacionEticaAsignacionTareasComponent } from './evaluacion-etica-crear/evaluacion-etica-asignacion-tareas/evaluacion-etica-asignacion-tareas.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    EvaluacionEticaListadoComponent,
    EvaluacionEticaCrearComponent,
    EvaluacionEticaDatosGeneralesComponent,
    EvaluacionEticaEquipoInvestigadorComponent,
    EvaluacionEticaAsignacionTareasComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    EvaluacionEticaRoutingModule,
    TranslateModule,
    MaterialDesignModule
  ]
})
export class EvaluacionEticaModule {
}
