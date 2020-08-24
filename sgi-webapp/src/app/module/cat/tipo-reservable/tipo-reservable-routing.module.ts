import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TipoReservableActualizarComponent } from './tipo-reservable-actualizar/tipo-reservable-actualizar.component';
import { TipoReservableCrearComponent } from './tipo-reservable-crear/tipo-reservable-crear.component';
import { TipoReservableListadoComponent } from './tipo-reservable-listado/tipo-reservable-listado.component';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('cat.tipo-reservable.listado.titulo');
const MSG_NEW_TITLE = marker('cat.tipo-reservable.crear.titulo');
const MSG_EDIT_TITLE = marker('cat.tipo-reservable.actualizar.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoReservableListadoComponent,
    data: {
      title: MSG_LISTADO_TITLE
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: TipoReservableCrearComponent,
    data: {
      title: MSG_NEW_TITLE
    }
  },
  {
    path: ':id',
    component: TipoReservableActualizarComponent,
    data: {
      title: MSG_EDIT_TITLE
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoReservableRoutingModule { }
