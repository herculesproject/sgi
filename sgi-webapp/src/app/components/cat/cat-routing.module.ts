import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@core/guards/auth.guard';
import { UrlUtils } from '@core/utils/url-utils';

import { CatRootComponent } from './cat-root/cat-root.component';

const routes: Routes = [
  {
    path: '', component: CatRootComponent, children: [
      {
        path: UrlUtils.cat.agrupacionServicios.valueOf(),
        loadChildren: () =>
          import('./agrupacion-servicio/agrupacion-servicio.module').then(
            (m) => m.AgrupacionServicioModule
          ),
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.cat.horario.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.cat.producto.valueOf(),
        component: null,
      },
      {
        path: UrlUtils.cat.solicitud.valueOf(),
        loadChildren: () =>
          import('./solicitud/solicitud.module').then(
            (m) => m.SolicitudModule
          ),
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.cat.tipoFungible.valueOf(),
        loadChildren: () =>
          import('./tipo-fungible/tipo-fungible.module').then(
            (m) => m.TipoFungibleModule
          ),
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.cat.tipoReservables.valueOf(),
        loadChildren: () =>
          import('./tipo-reservable/tipo-reservable.module').then(
            (m) => m.TipoReservableModule
          ),
        canActivate: [AuthGuard],
      },
      {
        path: UrlUtils.cat.unidadMedidas.valueOf(),
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
