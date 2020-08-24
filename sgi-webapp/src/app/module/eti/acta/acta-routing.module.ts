import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SgiAuthGuard } from '@sgi/framework/auth';

import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';

import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('eti.acta.listado.titulo');
const MSG_NEW_TITLE = marker('eti.acta.crear.titulo');
const MSG_EDIT_TITLE = marker('eti.acta.editar.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ActaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthorityForAnyUO: 'ETI-ACT-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: ActaCrearComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_NEW_TITLE,
      hasAuthorityForAnyUO: 'ETI-ACT-C'
    }
  },
  {
    path: `:id`,
    component: ActaEditarComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_EDIT_TITLE,
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
