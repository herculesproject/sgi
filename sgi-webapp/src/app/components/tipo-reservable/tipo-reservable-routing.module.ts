import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TipoReservableListadoComponent } from './tipo-reservable-listado/tipo-reservable-listado.component';
import { UrlUtils } from '@core/utils/url-utils';
import { TipoReservableCrearComponent } from './tipo-reservable-crear/tipo-reservable-crear.component';
import { TipoReservableActualizarComponent } from './tipo-reservable-actualizar/tipo-reservable-actualizar.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: TipoReservableListadoComponent,
  },
  {
    path: UrlUtils.agregar,
    component: TipoReservableCrearComponent,
  },
  {
    path: UrlUtils.actualizar + ':id',
    component: TipoReservableActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoReservableRoutingModule { }
