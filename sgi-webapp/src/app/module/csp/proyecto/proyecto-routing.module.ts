import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { ProyectoListadoComponent } from './proyecto-listado/proyecto-listado.component';
import { ProyectoEditarComponent } from './proyecto-editar/proyecto-editar.component';
import { PROYECTO_ROUTE_NAMES } from './proyecto-route-names';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoCrearComponent } from './proyecto-crear/proyecto-crear.component';
import { ActionGuard } from '@core/guards/master-form.guard';
import { ProyectoResolver } from './proyecto.resolver';
import { ProyectoFichaGeneralComponent } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.component';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ProyectoHitosComponent } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.component';

const MSG_LISTADO_TITLE = marker('csp.proyecto.listado.titulo');
const MSG_EDIT_TITLE = marker('csp.proyecto.editar.titulo');
const MSG_NEW_TITLE = marker('csp.proyecto.crear.titulo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: ProyectoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ProyectoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ROUTE_NAMES.FICHA_GENERAL
      },
      {
        path: PROYECTO_ROUTE_NAMES.FICHA_GENERAL,
        component: ProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.HITOS,
        component: ProyectoHitosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:id`,
    component: ProyectoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      proyecto: ProyectoResolver
    },
    data: {
      title: MSG_EDIT_TITLE
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ROUTE_NAMES.FICHA_GENERAL
      },
      {
        path: PROYECTO_ROUTE_NAMES.FICHA_GENERAL,
        component: ProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.HITOS,
        component: ProyectoHitosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoRoutingModule {
}
