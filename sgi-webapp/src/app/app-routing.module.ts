import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SgiAuthGuard } from '@sgi/framework/auth';
import { RootComponent } from '@shared/root/root.component';
import { APP_ROUTE_NAMES } from './app-route-names';
import { SgiRoutes } from '@core/route';

/**
 * Definimos las urls de la aplicación
 */
const routes: SgiRoutes = [
  {
    path: '',
    component: RootComponent
  },
  {
    path: APP_ROUTE_NAMES.CAT,
    loadChildren: () =>
      import('./module/cat/cat.module').then(
        (m) => m.CatModule
      ),
    canActivate: [SgiAuthGuard]
  },
  {
    path: APP_ROUTE_NAMES.ETI,
    loadChildren: () =>
      import('./module/eti/eti.module').then(
        (m) => m.EtiModule
      ),
    canActivate: [SgiAuthGuard]
  },
  {
    path: '**',
    component: RootComponent
  },
];

/**
 * Módulo para definir las url de entrada de la aplicación
 */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
