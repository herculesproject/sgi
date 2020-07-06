import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';

import { UnidadMedidaActualizarComponent } from './unidad-medida-actualizar/unidad-medida-actualizar.component';
import { UnidadMedidaCrearComponent } from './unidad-medida-crear/unidad-medida-crear.component';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';

const routes: Routes = [
  {
    path: UrlUtils.root.valueOf(),
    component: UnidadMedidaListadoComponent,
  },
  {
    path: UrlUtils.crear.valueOf(),
    component: UnidadMedidaCrearComponent,
  },
  {
    path: UrlUtils.actualizar.valueOf() + ':id',
    component: UnidadMedidaActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UnidadMedidaRoutingModule {}
