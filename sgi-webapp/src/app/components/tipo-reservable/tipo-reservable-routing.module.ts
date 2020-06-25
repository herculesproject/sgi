import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TipoReservableListadoComponent } from './tipo-reservable-listado/tipo-reservable-listado.component';
import { UrlUtils } from '@core/utils/url-utils';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: TipoReservableListadoComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoReservableRoutingModule { }
