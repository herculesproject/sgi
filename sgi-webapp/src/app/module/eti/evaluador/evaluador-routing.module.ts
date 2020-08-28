import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { EvaluadorListadoComponent } from './evaluador-listado/evaluador-listado.component';
import { EvaluadorCrearComponent } from './evaluador-crear/evaluador-crear.component';
import { EvaluadorEditarComponent } from './evaluador-editar/evaluador-editar.component';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ROUTE_NAMES } from '@core/route.names';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('eti.evaluador.listado.titulo');
const MSG_NEW_TITLE = marker('eti.evaluador.crear.titulo');
const MSG_EDIT_TITLE = marker('eti.evaluador.actualizar.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: EvaluadorListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthorityForAnyUO: 'ETI-EVR-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: EvaluadorCrearComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_NEW_TITLE,
      hasAuthorityForAnyUO: 'ETI-EVR-C'
    }
  },
  {
    path: ':id',
    component: EvaluadorEditarComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'ETI-EVR-E'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EvaluadorRoutingModule {
}
