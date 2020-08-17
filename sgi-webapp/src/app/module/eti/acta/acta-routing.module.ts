import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SgiAuthRoutes, SgiAuthGuard } from '@sgi/framework/auth';

import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';

import { ROUTE_NAMES } from '@core/route.names';

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: ActaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAuthorityForAnyUO: 'ETI-ACT-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ActaCrearComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAuthorityForAnyUO: 'ETI-ACT-C'
    }
  },
  {
    path: `${ROUTE_NAMES.EDIT}/:id`,
    component: ActaEditarComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAuthorityForAnyUO: 'ETI-ACT-E'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ActaRoutingModule {
}
