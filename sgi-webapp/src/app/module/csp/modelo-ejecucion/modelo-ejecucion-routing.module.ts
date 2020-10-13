import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ModeloEjecucionCrearComponent } from './modelo-ejecucion-crear/modelo-ejecucion-crear.component';
import { ModeloEjecucionDatosGeneralesComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-datos-generales/modelo-ejecucion-datos-generales.component';
import { ModeloEjecucionListadoComponent } from './modelo-ejecucion-listado/modelo-ejecucion-listado.component';
import { MODELO_EJECUCION_ROUTE_NAMES } from './modelo-ejecucion-route-names';

const MSG_LISTADO_TITLE = marker('csp.modelo.ejecucion.listado.titulo');
const MSG_NEW_TITLE = marker('csp.modelo.ejecucion.crear.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ModeloEjecucionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE
    },
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ModeloEjecucionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: MODELO_EJECUCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: MODELO_EJECUCION_ROUTE_NAMES.DATOS_GENERALES,
        component: ModeloEjecucionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ModeloEjecuccionRoutingModule {
}
