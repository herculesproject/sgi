import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { EjecucionEconomicaDataResolver, EJECUCION_ECONOMICA_DATA_KEY } from './ejecucion-economica-data.resolver';
import { EjecucionEconomicaEditarComponent } from './ejecucion-economica-editar/ejecucion-economica-editar.component';
import { ProyectosComponent } from './ejecucion-economica-formulario/proyectos/proyectos.component';
import { EjecucionEconomicaListadoComponent } from './ejecucion-economica-listado/ejecucion-economica-listado.component';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from './ejecucion-economica-route-names';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';

const EJECUCION_ECONOMICA_KEY = marker('menu.csp.ejecucion-economica');

const routes: SgiRoutes = [
  {
    path: '',
    component: EjecucionEconomicaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: EJECUCION_ECONOMICA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-EJEC-V', 'CSP-EJEC-E']
    }
  },
  {
    path: `:${EJECUCION_ECONOMICA_ROUTE_PARAMS.ID}`,
    component: EjecucionEconomicaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: EJECUCION_ECONOMICA_KEY,
      hasAnyAuthorityForAnyUO: ['CSP-EJEC-V', 'CSP-EJEC-E']
    },
    resolve: {
      [EJECUCION_ECONOMICA_DATA_KEY]: EjecucionEconomicaDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EJECUCION_ECONOMICA_ROUTE_NAMES.PROYECTOS
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.PROYECTOS,
        component: ProyectosComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EjecucionEconomicaRoutingModule {
}
