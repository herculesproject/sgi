import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';

import { TipoReservableActualizarComponent } from './tipo-reservable-actualizar/tipo-reservable-actualizar.component';
import { TipoReservableCrearComponent } from './tipo-reservable-crear/tipo-reservable-crear.component';
import { TipoReservableListadoComponent } from './tipo-reservable-listado/tipo-reservable-listado.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: TipoReservableListadoComponent,
  },
  {
    path: UrlUtils.crear.valueOf(),
    component: TipoReservableCrearComponent,
  },
  {
    path: UrlUtils.actualizar.valueOf() + ':id',
    component: TipoReservableActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoReservableRoutingModule { }
