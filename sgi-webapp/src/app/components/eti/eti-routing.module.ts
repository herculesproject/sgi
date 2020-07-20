import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {EtiRootComponent} from './eti-root/eti-root.component';
import {UrlUtils} from '@core/utils/url-utils';
import {AuthGuard} from '@core/guards/auth.guard';

const routes: Routes = [
  {
    path: '', component: EtiRootComponent, children: [
      {
        path: UrlUtils.datos.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.cvn.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.produccionCientifica.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.ofertaTecnologica.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.solicitudesConvocatoria.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.proyectos.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.propiedadIntelectual.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.empresasBaseTecnologica.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.gruposInvestigacion.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.areaEspecializacion.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.evaluacionEtica.valueOf(),
        loadChildren: () =>
          import('./evaluacion-etica/evaluacion-etica.module').then(
            (m) => m.EvaluacionEticaModule
          ),
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.solicitudesSAI.valueOf(),
        component: null,
      },
      {path: '**', component: null},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EtiRoutingModule {
}
