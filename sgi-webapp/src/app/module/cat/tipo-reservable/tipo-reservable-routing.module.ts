import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TipoReservableActualizarComponent } from './tipo-reservable-actualizar/tipo-reservable-actualizar.component';
import { TipoReservableCrearComponent } from './tipo-reservable-crear/tipo-reservable-crear.component';
import { TipoReservableListadoComponent } from './tipo-reservable-listado/tipo-reservable-listado.component';
import { ROUTE_NAMES } from '@core/route.names';

const routes: Routes = [
  {
    path: '',
    component: TipoReservableListadoComponent,
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: TipoReservableCrearComponent,
  },
  {
    path: ':id',
    component: TipoReservableActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoReservableRoutingModule { }
