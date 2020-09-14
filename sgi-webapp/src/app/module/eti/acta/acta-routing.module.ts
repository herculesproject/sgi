import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SgiAuthGuard } from '@sgi/framework/auth';

import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';

import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';
import { ActaDatosGeneralesComponent } from './acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from './acta-formulario/acta-memorias/acta-memorias.component';

import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActaResolver } from './acta.resolver';
import { ActionGuard } from '@core/guards/master-form.guard';
import { ACTA_ROUTE_NAMES } from './acta-route-names';
import { ActaAsistentesListadoComponent } from './acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';

const MSG_LISTADO_TITLE = marker('eti.acta.listado.titulo');
const MSG_NEW_TITLE = marker('eti.acta.crear.titulo');
const MSG_EDIT_TITLE = marker('eti.acta.editar.titulo');



const routes: SgiRoutes = [
  {
    path: '',
    component: ActaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthorityForAnyUO: 'ETI-ACT-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ActaCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      hasAuthorityForAnyUO: 'ETI-ACT-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: ACTA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: ACTA_ROUTE_NAMES.DATOS_GENERALES,
        component: ActaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: ACTA_ROUTE_NAMES.MEMORIAS,
        component: ActaMemoriasComponent
      },
      {
        path: ACTA_ROUTE_NAMES.ASISTENTES,
        component: ActaAsistentesListadoComponent
      }
    ]
  },
  {
    path: `:id`,
    component: ActaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      acta: ActaResolver
    },
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'ETI-ACT-E'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: ACTA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: ACTA_ROUTE_NAMES.DATOS_GENERALES,
        component: ActaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: ACTA_ROUTE_NAMES.MEMORIAS,
        component: ActaMemoriasComponent
      },
      {
        path: ACTA_ROUTE_NAMES.ASISTENTES,
        component: ActaAsistentesListadoComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ActaRoutingModule {
}
