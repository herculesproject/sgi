import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';

import { RootComponent } from './components/root/root.component';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';

/**
 * Definimos las urls de la aplicación
 */
const routes: SgiAuthRoutes = [
  {
    path: UrlUtils.cat.root.valueOf(),
    loadChildren: () =>
      import('./components/cat/cat.module').then(
        (m) => m.CatModule
      ),
    canActivate: [SgiAuthGuard],
  },
  {
    path: UrlUtils.ebt.root.valueOf(),
    loadChildren: () =>
      import('./components/ebt/ebt.module').then(
        (m) => m.EbtModule
      ),
    canActivate: [SgiAuthGuard],
  },
  {
    path: UrlUtils.pii.root.valueOf(),
    loadChildren: () =>
      import('./components/pii/pii.module').then(
        (m) => m.PiiModule
      ),
    canActivate: [SgiAuthGuard],
  },
  {
    path: UrlUtils.eti.root.valueOf(),
    loadChildren: () =>
      import('./components/eti/eti.module').then(
        (m) => m.EtiModule
      ),
    canActivate: [SgiAuthGuard],
  },
  { path: '', redirectTo: UrlUtils.eti.root.valueOf(), pathMatch: 'full' },
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
