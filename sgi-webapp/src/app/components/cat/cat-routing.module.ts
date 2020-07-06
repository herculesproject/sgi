import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@core/guards/auth.guard';
import { UrlUtils } from '@core/utils/url-utils';

import { CatRootComponent } from './cat-root/cat-root.component';

const routes: Routes = [
  {
    path: '', component: CatRootComponent, children: [
      {
        path: UrlUtils.agrupacionServicios.valueOf(),
        loadChildren: () =>
          import('./agrupacion-servicio/agrupacion-servicio.module').then(
            (m) => m.AgrupacionServicioModule
          )
      },
      {
        path: UrlUtils.horario.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.producto.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.solicitud.valueOf(),
        loadChildren: () =>
          import('./solicitud/solicitud.module').then(
            (m) => m.SolicitudModule
          )
      },
      {
        path: UrlUtils.tipoFungible.valueOf(),
        loadChildren: () =>
          import('./tipo-fungible/tipo-fungible.module').then(
            (m) => m.TipoFungibleModule
          ),
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.tipoReservables.valueOf(),
        loadChildren: () =>
          import('./tipo-reservable/tipo-reservable.module').then(
            (m) => m.TipoReservableModule
          ),
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.unidadMedida.valueOf(),
        loadChildren: () =>
          import('./unidad-medida/unidad-medida.module').then(
            (m) => m.UnidadMedidaModule
          ),
        canActivate: [AuthGuard],
      },
      { path: '**', component: null },
    ]
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CatRoutingModule { }
