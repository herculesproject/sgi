import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';

import { EvaluacionListadoComponent } from './evaluacion-listado/evaluacion-listado.component';

const routes: SgiAuthRoutes = [
  {
    path: UrlUtils.root.valueOf(),
    component: EvaluacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAnyAuthorityForAnyUO: ['ETI-EVC-V', 'ETI-EVC-VR']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EvaluacionRoutingModule {
}
