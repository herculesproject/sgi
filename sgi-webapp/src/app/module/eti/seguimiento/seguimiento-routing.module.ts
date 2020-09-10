import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';

import { SeguimientoListadoComponent } from './seguimiento-listado/seguimiento-listado.component';

const MSG_LISTADO_TITLE = marker('eti.seguimiento.listado.titulo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: SeguimientoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-VR', 'ETI-EVC-EVALR']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SeguimientoRoutingModule {
}
