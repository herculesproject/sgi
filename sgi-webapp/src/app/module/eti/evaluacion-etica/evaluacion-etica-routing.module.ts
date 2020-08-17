import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EvaluacionEticaListadoComponent } from './evaluacion-etica-listado/evaluacion-etica-listado.component';
import { EvaluacionEticaCrearComponent } from './evaluacion-etica-crear/evaluacion-etica-crear.component';
import { ROUTE_NAMES } from '@core/route.names';

const routes: Routes = [
  {
    path: '',
    component: EvaluacionEticaListadoComponent,
  },
  {
    path: ROUTE_NAMES.NEW,
    component: EvaluacionEticaCrearComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EvaluacionEticaRoutingModule {
}
