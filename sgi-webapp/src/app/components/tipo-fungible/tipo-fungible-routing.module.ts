import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TipoFungibleListadoComponent } from './tipo-fungible-listado/tipo-fungible-listado.component';
import { UrlUtils } from '@core/utils/url-utils';
import { TipoFungibleAgregarActualizarComponent } from './tipo-fungible-agregar-actualizar/tipo-fungible-agregar-actualizar.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: TipoFungibleListadoComponent,
  },
  {
    path: UrlUtils.agregar,
    component: TipoFungibleAgregarActualizarComponent,
  },
  {
    path: UrlUtils.actualizar + ':id',
    component: TipoFungibleAgregarActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoFungibleRoutingModule { }
