import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { UnidadMedidaActualizarComponent } from './unidad-medida-actualizar/unidad-medida-actualizar.component';
import { UnidadMedidaCrearComponent } from './unidad-medida-crear/unidad-medida-crear.component';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('cat.unidad-medida.listado.titulo');
const MSG_NEW_TITLE = marker('cat.unidad-medida.crear.titulo');
const MSG_EDIT_TITLE = marker('cat.unidad-medida.actualizar.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: UnidadMedidaListadoComponent,
    data: {
      title: MSG_LISTADO_TITLE
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: UnidadMedidaCrearComponent,
    data: {
      title: MSG_NEW_TITLE
    }
  },
  {
    path: ':id',
    component: UnidadMedidaActualizarComponent,
    data: {
      title: MSG_EDIT_TITLE
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UnidadMedidaRoutingModule { }
