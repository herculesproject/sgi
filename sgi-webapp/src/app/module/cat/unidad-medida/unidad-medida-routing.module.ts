import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UnidadMedidaActualizarComponent } from './unidad-medida-actualizar/unidad-medida-actualizar.component';
import { UnidadMedidaCrearComponent } from './unidad-medida-crear/unidad-medida-crear.component';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';
import { ROUTE_NAMES } from '@core/route.names';

const routes: Routes = [
  {
    path: '',
    component: UnidadMedidaListadoComponent,
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: UnidadMedidaCrearComponent,
  },
  {
    path: ':id',
    component: UnidadMedidaActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UnidadMedidaRoutingModule { }
