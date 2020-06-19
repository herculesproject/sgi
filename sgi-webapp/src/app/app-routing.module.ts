import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RootComponent } from './components/root/root.component';
import { AuthGuard } from '@core/guards/auth.guard';
import { UrlUtils } from '@core/utils/url-utils';

/**
 * Definimos las urls de la aplicación
 */
const routes: Routes = [
  {
    path: UrlUtils.agrupacionServicios.valueOf(),
    component: RootComponent,
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
    component: RootComponent,
  },
  {
    path: UrlUtils.tipoFungible.valueOf(),
    loadChildren: () =>
      import('./components/tipo-fungible/tipo-fungible.module').then(m => m.TipoFungibleModule),
    canActivate: [AuthGuard]
  },
  {
    path: UrlUtils.tipoReservables.valueOf(),
    component: RootComponent,
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
