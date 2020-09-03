import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';

import { GestionEvaluacionListadoComponent } from './gestion-evaluacion-listado/gestion-evaluacion-listado.component';

const MSG_GESTION_LISTADO_TITLE = marker('eti.gestionEvaluacion.listado.titulo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: GestionEvaluacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_GESTION_LISTADO_TITLE,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-V']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GestionEvaluacionRoutingModule {
}
