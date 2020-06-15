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
    path: UrlUtils.agrupacionServicios,
    component: RootComponent,
  },
  {
    path: UrlUtils.horario,
    component: RootComponent,
  },
  {
    path: UrlUtils.producto,
    component: RootComponent,
  },
  {
    path: UrlUtils.solicitud,
    component: RootComponent,
  },
  {
    path: UrlUtils.tipoFungible,
    component: RootComponent,
  },
  {
    path: UrlUtils.tipoReservables,
    component: RootComponent,
  },
  {
    path: UrlUtils.unidadMedida,
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
export class AppRoutingModule {}
