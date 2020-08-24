import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TipoFungibleActualizarComponent } from './tipo-fungible-actualizar/tipo-fungible-actualizar.component';
import { TipoFungibleCrearComponent } from './tipo-fungible-crear/tipo-fungible-crear.component';
import { TipoFungibleListadoComponent } from './tipo-fungible-listado/tipo-fungible-listado.component';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('cat.tipo-fungible.actualizar.titulo');
const MSG_NEW_TITLE = marker('cat.tipo-fungible.crear.titulo');
const MSG_EDIT_TITLE = marker('cat.tipo-fungible.listado.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoFungibleListadoComponent,
    data: {
      title: MSG_LISTADO_TITLE
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: TipoFungibleCrearComponent,
    data: {
      title: MSG_NEW_TITLE
    }
  },
  {
    path: ':id',
    component: TipoFungibleActualizarComponent,
    data: {
      title: MSG_EDIT_TITLE
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoFungibleRoutingModule { }
