import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';
import { UnidadMedidaAgregarActualizarComponent } from './unidad-medida-agregar-actualizar/unidad-medida-agregar-actualizar.component';
import { UrlUtils } from '@core/utils/url-utils';

const routes: Routes = [
  {
    path: UrlUtils.root,
    component: UnidadMedidaListadoComponent,
  },
  {
    path: UrlUtils.agregar,
    component: UnidadMedidaAgregarActualizarComponent,
  },
  {
    path: UrlUtils.actualizar,
    component: UnidadMedidaAgregarActualizarComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UnidadMedidaRoutingModule {}
