import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';

import { InvRootComponent } from './inv-root/inv-root.component';
import { INV_ROUTE_NAMES } from './inv-route-names';
import { InicioComponent } from './inicio/inicio.component';

const MSG_ROOT_TITLE = marker('inv.root.title');
const MSG_EVALUACIONES_TITLE = marker('menu.principal.inv.evaluaciones');
const MSG_SEGUIMIENTOS_TITLE = marker('menu.principal.inv.seguimientos');
const MSG_PETICIONES_EVALUACION_TITLE = marker('menu.principal.inv.peticionesEvaluacion');

const routes: SgiRoutes = [
  {
    path: '',
    component: InvRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: InicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      {
        path: INV_ROUTE_NAMES.EVALUACIONES,
        loadChildren: () =>
          import('../eti/evaluacion/evaluacion.module').then(
            (m) => m.EvaluacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_EVALUACIONES_TITLE,
          hasAnyAuthorityForAnyUO: ['ETI-EVC-VR-INV', 'ETI-EVC-EVALR-INV']
        }
      },
      {
        path: INV_ROUTE_NAMES.SEGUIMIENTOS,
        loadChildren: () =>
          import('../eti/seguimiento/seguimiento.module').then(
            (m) => m.EvaluacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SEGUIMIENTOS_TITLE,
          hasAnyAuthorityForAnyUO: ['ETI-EVC-VR-INV', 'ETI-EVC-EVALR-INV']
        }
      },
      {
        path: INV_ROUTE_NAMES.PETICIONES_EVALUACION,
        loadChildren: () =>
          import('../eti/peticion-evaluacion/peticion-evaluacion-inv.module').then(
            (m) => m.PeticionEvaluacionInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PETICIONES_EVALUACION_TITLE,
          hasAnyAuthorityForAnyUO: ['ETI-PEV-VR-INV', 'ETI-PEV-C-INV', 'ETI-PEV-ER-INV', 'ETI-PEV-BR-INV']
        }
      },
      { path: '**', component: null }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InvRoutingModule {
}
