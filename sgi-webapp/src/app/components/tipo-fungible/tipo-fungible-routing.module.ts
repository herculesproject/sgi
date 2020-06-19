import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TipoFungibleListadoComponent } from './tipo-fungible-listado/tipo-fungible-listado.component';
import { UrlUtils } from '@core/utils/url-utils';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: TipoFungibleListadoComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoFungibleRoutingModule { }
