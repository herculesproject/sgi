import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CatRootComponent } from './cat-root/cat-root.component';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { CAT_ROUTE_NAMES } from './cat-route-names';

const routes: SgiAuthRoutes = [
  {
    path: '', component: CatRootComponent, children: [
      {
        path: CAT_ROUTE_NAMES.AGRUPACION_SERVICIO,
        loadChildren: () =>
          import('./agrupacion-servicio/agrupacion-servicio.module').then(
            (m) => m.AgrupacionServicioModule
          ),
        canActivate: [SgiAuthGuard],
      },
      {
        path: CAT_ROUTE_NAMES.HORARIO,
        component: null,
      },
      {
        path: CAT_ROUTE_NAMES.PRODUCTO,
        component: null,
      },
      {
        path: CAT_ROUTE_NAMES.SOLICITUD,
        loadChildren: () =>
          import('./solicitud/solicitud.module').then(
            (m) => m.SolicitudModule
          ),
        canActivate: [SgiAuthGuard],
      },
      {
        path: CAT_ROUTE_NAMES.TIPO_FUNGIBLE,
        loadChildren: () =>
          import('./tipo-fungible/tipo-fungible.module').then(
            (m) => m.TipoFungibleModule
          ),
        canActivate: [SgiAuthGuard],
      },
      {
        path: CAT_ROUTE_NAMES.TIPO_RESERVABLE,
        loadChildren: () =>
          import('./tipo-reservable/tipo-reservable.module').then(
            (m) => m.TipoReservableModule
          ),
        canActivate: [SgiAuthGuard],
      },
      {
        path: CAT_ROUTE_NAMES.UNIDAD_MEDIDA,
        loadChildren: () =>
          import('./unidad-medida/unidad-medida.module').then(
            (m) => m.UnidadMedidaModule
          ),
        canActivate: [SgiAuthGuard],
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
