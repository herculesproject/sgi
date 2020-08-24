import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SgiRoutes } from '@core/route';
import { InicioComponent } from './inicio/inicio.component';
import { ETI_ROUTE_NAMES } from './eti-route-names';

import { EtiRootComponent } from './eti-root/eti-root.component';

const MSG_ROOT_TITLE = marker('eti.root.title');
const MSG_SOLICITUDES_CONVOCATORIA_TITLE = marker('menu.principal.eti.solicitudesConvocatoria');
const MSG_EVALUACIONES_TITLE = marker('menu.principal.eti.evaluaciones');
const MSG_ACTAS_TITLE = marker('menu.principal.eti.acta');

const routes: SgiRoutes = [
  {
    path: '',
    component: EtiRootComponent,
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
        path: ETI_ROUTE_NAMES.SOLICITUDES_CONVOCATORIA,
        loadChildren: () =>
          import('./convocatoria-reunion/convocatoria-reunion.module').then(
            (m) => m.ConvocatoriaReunionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SOLICITUDES_CONVOCATORIA_TITLE,
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
          title: MSG_EVALUACIONES_TITLE,
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
          title: MSG_ACTAS_TITLE,
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
