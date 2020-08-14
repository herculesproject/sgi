import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UrlUtils} from '@core/utils/url-utils';
import {EvaluacionEticaListadoComponent} from './evaluacion-etica-listado/evaluacion-etica-listado.component';
import {EvaluacionEticaCrearComponent} from './evaluacion-etica-crear/evaluacion-etica-crear.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: EvaluacionEticaListadoComponent,
  },
  {
    path: UrlUtils.crear.valueOf(),
    component: EvaluacionEticaCrearComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EvaluacionEticaRoutingModule {
}
