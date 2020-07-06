import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuard} from '@core/guards/auth.guard';
import {UrlUtils} from '@core/utils/url-utils';
import {RootComponent} from './components/root/root.component';

/**
 * Definimos las urls de la aplicación
 */
const routes: Routes = [
  {
    path: UrlUtils.cat.valueOf(),
    loadChildren: () =>
      import('./components/cat/cat.module').then(
        (m) => m.CatModule
      ),
    canActivate: [AuthGuard],
  },
  {
    path: UrlUtils.ebt.valueOf(),
    loadChildren: () =>
    import('./components/ebt/ebt.module').then(
      (m) => m.EbtModule
    ),
    canActivate: [AuthGuard],
  },
  {
    path: UrlUtils.pii.valueOf(),
    loadChildren: () =>
      import('./components/pii/pii.module').then(
        (m) => m.PiiModule
      ),
    canActivate: [AuthGuard],
  },
  {path: '', redirectTo: UrlUtils.cat.valueOf(), pathMatch: 'full'},
  {path: '**', component: RootComponent},
];

/**
 * Módulo para definir las url de entrada de la aplicación
 */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
