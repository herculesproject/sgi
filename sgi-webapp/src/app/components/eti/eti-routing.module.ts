import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';

import { EtiRootComponent } from './eti-root/eti-root.component';

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: EtiRootComponent,
    children: [
      {
        path: UrlUtils.eti.datos.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.cvn.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.produccionCientifica.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.ofertaTecnologica.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.solicitudesConvocatoria.valueOf(),
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
        path: UrlUtils.eti.evaluacion.valueOf(),
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
        path: UrlUtils.eti.proyectos.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.propiedadIntelectual.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.empresasBaseTecnologica.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.gruposInvestigacion.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.areaEspecializacion.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.eti.actas.valueOf(),
        loadChildren: () =>
          import('./acta/acta.module').then(
            (m) => m.ActaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          hasAnyAuthorityForAnyUO: ['ETI-ACT-V', 'ETI-ACT-C', 'ETI-ACT-E', 'ETI-ACT-B', 'ETI-ACT-DES', 'ETI-ACT-FIN']
        }
      },
      {
        path: UrlUtils.eti.evaluacionEtica.valueOf(),
        loadChildren: () =>
          import('./evaluacion-etica/evaluacion-etica.module').then(
            (m) => m.EvaluacionEticaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          hasAnyAuthorityForAnyUO: ['ETI-EVC-VR', 'ETI-EVC-C', 'ETI-EVC-E', 'ETI-EVC-B', 'ETI-EVC-ENV']
        }
      },
      {
        path: UrlUtils.eti.solicitudesSAI.valueOf(),
        component: null,
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
