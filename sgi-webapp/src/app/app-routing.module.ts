import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '@core/guards/auth.guard';
import { UrlUtils } from '@core/utils/url-utils';

import { RootComponent } from './components/root/root.component';

/**
 * Definimos las urls de la aplicación
 */
const routes: Routes = [
  {
    path: UrlUtils.agrupacionServicios.valueOf(),
    loadChildren: () =>
      import('./components/agrupacion-servicio/agrupacion-servicio.module').then(m => m.AgrupacionServicioModule),
    canActivate: [AuthGuard]
  },
  {
    path: UrlUtils.horario.valueOf(),
    component: RootComponent,
  },
  {
    path: UrlUtils.producto.valueOf(),
    component: RootComponent,
  },
  {
    path: UrlUtils.solicitud.valueOf(),
    loadChildren: () =>
      import('./components/solicitud/solicitud.module').then(m => m.SolicitudModule),
    canActivate: [AuthGuard]
  },
  {
    path: UrlUtils.tipoFungible.valueOf(),
    loadChildren: () =>
      import('./components/tipo-fungible/tipo-fungible.module').then(m => m.TipoFungibleModule),
    canActivate: [AuthGuard]
  },
  {
    path: UrlUtils.tipoReservables.valueOf(),
    loadChildren: () =>
      import('./components/tipo-reservable/tipo-reservable.module').then(m => m.TipoReservableModule),
    canActivate: [AuthGuard]
  },
  {
    path: UrlUtils.unidadMedida.valueOf(),
    loadChildren: () =>
      import('./components/unidad-medida/unidad-medida.module').then(
        (m) => m.UnidadMedidaModule
      ),
    canActivate: [AuthGuard],
  },
  /**
   * Si no reconoce la url
   */
  { path: '**', component: RootComponent },
];

/**
 * Módulo para definir las url de entrada de la aplicación
 */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
