import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@core/guards/auth.guard';
import { UrlUtils } from '@core/utils/url-utils';

import { EtiRootComponent } from './eti-root/eti-root.component';

const routes: Routes = [
  {
    path: '', component: EtiRootComponent, children: [
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
        canActivate: [AuthGuard],
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
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.eti.evaluacionEtica.valueOf(),
        loadChildren: () =>
          import('./evaluacion-etica/evaluacion-etica.module').then(
            (m) => m.EvaluacionEticaModule
          ),
        canActivate: [AuthGuard],
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
