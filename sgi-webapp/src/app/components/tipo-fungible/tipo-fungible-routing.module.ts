import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';

import { TipoFungibleActualizarComponent } from './tipo-fungible-actualizar/tipo-fungible-actualizar.component';
import { TipoFungibleCrearComponent } from './tipo-fungible-crear/tipo-fungible-crear.component';
import { TipoFungibleListadoComponent } from './tipo-fungible-listado/tipo-fungible-listado.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: TipoFungibleListadoComponent,
  },
  {
    path: UrlUtils.agregar,
    component: TipoFungibleCrearComponent,
  },
  {
    path: UrlUtils.actualizar + ':id',
    component: TipoFungibleActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoFungibleRoutingModule { }
