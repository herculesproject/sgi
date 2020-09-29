import { SgiRoutes } from '@core/route';

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SgiAuthGuard } from '@sgi/framework/auth';

import { marker } from '@biesbjerg/ngx-translate-extract-marker';

import { ConvocatoriaListadoComponent } from './convocatoria-listado/convocatoria-listado.component';
import { ROUTE_NAMES } from '@core/route.names';
import { ActionGuard } from '@core/guards/master-form.guard';
import { ConvocatoriaCrearComponent } from './convocatoria-crear/convocatoria-crear.component';
import { CONVOCATORIA_ROUTE_NAMES } from './convocatoria-route-names';
import { ConvocatoriaDatosGeneralesComponent } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.component';
import { FragmentGuard } from '@core/guards/detail-form.guard';


const MSG_LISTADO_TITLE = marker('csp.convocatoria.listado.titulo');
const MSG_NEW_TITLE = marker('csp.convocatoria.crear.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ConvocatoriaCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES
      }, {
        path: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConvocatoriaRoutingModule {
}
