import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TipoFungibleActualizarComponent } from './tipo-fungible-actualizar/tipo-fungible-actualizar.component';
import { TipoFungibleCrearComponent } from './tipo-fungible-crear/tipo-fungible-crear.component';
import { TipoFungibleListadoComponent } from './tipo-fungible-listado/tipo-fungible-listado.component';
import { ROUTE_NAMES } from '@core/route.names';

const routes: Routes = [
  {
    path: '',
    component: TipoFungibleListadoComponent,
  },
  {
    path: ROUTE_NAMES.NEW,
    component: TipoFungibleCrearComponent,
  },
  {
    path: `${ROUTE_NAMES.EDIT}/:id`,
    component: TipoFungibleActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoFungibleRoutingModule { }
