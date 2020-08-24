import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';

import { EvaluacionListadoComponent } from './evaluacion-listado/evaluacion-listado.component';

const MSG_LISTADO_TITLE = marker('eti.evaluacion.listado.titulo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: EvaluacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
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
