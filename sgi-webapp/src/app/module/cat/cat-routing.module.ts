import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CatRootComponent } from './cat-root/cat-root.component';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { CAT_ROUTE_NAMES } from './cat-route-names';
import { SgiRoutes } from '@core/route';

const MSG_ROOT_TITLE = marker('cat.root.title');
const MSG_AGRUPACIONES_SERVICIO_TITLE = marker('menu.principal.cat.agrupaciones-servicios');
const MSG_SOLICITUD_TITLE = marker('menu.principal.cat.solicitudes');
const MSG_TIPOS_FUNGIBLE_TITLE = marker('menu.principal.cat.tipos-fungibles');
const MSG_TIPOS_RESERVABLE_TITLE = marker('menu.principal.cat.tipos-reservables');
const MSG_UNIDADES_MEDIDA_TITLE = marker('menu.principal.cat.unidades-medida');

const routes: SgiRoutes = [
  {
    path: '',
    component: CatRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: CAT_ROUTE_NAMES.AGRUPACIONES_SERVICIO,
        loadChildren: () =>
          import('./agrupacion-servicio/agrupacion-servicio.module').then(
            (m) => m.AgrupacionServicioModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_AGRUPACIONES_SERVICIO_TITLE
        }
      },
      {
        path: CAT_ROUTE_NAMES.SOLICITUD,
        loadChildren: () =>
          import('./solicitud/solicitud.module').then(
            (m) => m.SolicitudModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SOLICITUD_TITLE
        }
      },
      {
        path: CAT_ROUTE_NAMES.TIPOS_FUNGIBLE,
        loadChildren: () =>
          import('./tipo-fungible/tipo-fungible.module').then(
            (m) => m.TipoFungibleModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPOS_FUNGIBLE_TITLE
        }
      },
      {
        path: CAT_ROUTE_NAMES.TIPOS_RESERVABLE,
        loadChildren: () =>
          import('./tipo-reservable/tipo-reservable.module').then(
            (m) => m.TipoReservableModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPOS_RESERVABLE_TITLE
        }
      },
      {
        path: CAT_ROUTE_NAMES.UNIDADES_MEDIDA,
        loadChildren: () =>
          import('./unidad-medida/unidad-medida.module').then(
            (m) => m.UnidadMedidaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_UNIDADES_MEDIDA_TITLE
        }
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
