import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SharedModule} from '@shared/shared.module';
import {TranslateModule} from '@ngx-translate/core';
import {MaterialDesignModule} from '@material/material-design.module';
import {ComponentsModule} from '../../components.module';
import {EvaluacionEticaRoutingModule} from './evaluacion-etica-routing.module';
import {EvaluacionEticaListadoComponent} from './evaluacion-etica-listado/evaluacion-etica-listado.component';
import {EvaluacionEticaCrearComponent} from './evaluacion-etica-crear/evaluacion-etica-crear.component';
import {PerfectScrollbarModule} from 'ngx-perfect-scrollbar';
import {EvaluacionEticaDatosGeneralesComponent} from './evaluacion-etica-crear/evaluacion-etica-datos-generales/evaluacion-etica-datos-generales.component';
import {EvaluacionEticaEquipoInvestigadorComponent} from './evaluacion-etica-crear/evaluacion-etica-equipo-investigador/evaluacion-etica-equipo-investigador.component';
import {EvaluacionEticaAsignacionTareasComponent} from './evaluacion-etica-crear/evaluacion-etica-asignacion-tareas/evaluacion-etica-asignacion-tareas.component';


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
    EvaluacionEticaRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    ComponentsModule,
    PerfectScrollbarModule,
  ]
})
export class EvaluacionEticaModule {
}
