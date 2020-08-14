import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { UrlUtils } from '@core/utils/url-utils';

import { SgiAuthRoutes, SgiAuthGuard } from '@sgi/framework/auth';

import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';


const routes: SgiAuthRoutes = [
  {
    path: UrlUtils.root.valueOf(),
    component: ActaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAuthorityForAnyUO: 'ETI-ACT-V'
    }
  },
  {
    path: UrlUtils.crear.valueOf(),
    component: ActaCrearComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAuthorityForAnyUO: 'ETI-ACT-C'
    }
  },
  {
    path: UrlUtils.actualizar.valueOf() + ':id',
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
