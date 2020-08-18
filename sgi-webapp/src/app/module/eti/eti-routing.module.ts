import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { InicioComponent } from './inicio/inicio.component';
import { ETI_ROUTE_NAMES } from './eti-route-names';

import { EtiRootComponent } from './eti-root/eti-root.component';

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: EtiRootComponent,
    children: [
      {
        path: '',
        component: InicioComponent,
        pathMatch: 'full'
      },
      {
        path: ETI_ROUTE_NAMES.SOLICITUDES_CONVOCATORIA,
        loadChildren: () =>
          import('./convocatoria-reunion/convocatoria-reunion.module').then(
            (m) => m.ConvocatoriaReunionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          hasAnyAuthorityForAnyUO: ['ETI-CNV-V', 'ETI-CNV-C', 'ETI-CNV-E', 'ETI-CNV-B', 'ETI-CNV-ENV']
        }
      },
      {
        path: ETI_ROUTE_NAMES.EVALUACIONES,
        loadChildren: () =>
          import('./evaluacion/evaluacion.module').then(
            (m) => m.EvaluacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          hasAnyAuthorityForAnyUO: [
            'ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-C', 'ETI-EVC-E', 'ETI-EVC-B',
            'ETI-EVC-EVAL', 'ETI-EVC-EVALR'
          ]
        }
      },
      {
        path: ETI_ROUTE_NAMES.ACTAS,
        loadChildren: () =>
          import('./acta/acta.module').then(
            (m) => m.ActaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          hasAnyAuthorityForAnyUO: ['ETI-ACT-V', 'ETI-ACT-C', 'ETI-ACT-E', 'ETI-ACT-B', 'ETI-ACT-DES', 'ETI-ACT-FIN']
        }
      },
      { path: '**', component: null },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EtiRoutingModule {
}
