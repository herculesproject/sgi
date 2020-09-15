import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SgiAuthGuard } from '@sgi/framework/auth';
import { RootComponent } from '@shared/root/root.component';
import { APP_ROUTE_NAMES } from './app-route-names';
import { SgiRoutes } from '@core/route';
import { DenyInvestigadorGuard } from '@core/guards/deny-investigador.guard';
import { AllowInvestigadorGuard } from '@core/guards/allow-investigador.guard';

/**
 * Definimos las urls de la aplicación
 */
const routes: SgiRoutes = [
  {
    path: '',
    pathMatch: 'full',
    component: RootComponent,
    canActivate: [SgiAuthGuard, DenyInvestigadorGuard]
  },
  {
    path: APP_ROUTE_NAMES.ETI,
    loadChildren: () =>
      import('./module/eti/eti.module').then(
        (m) => m.EtiModule
      ),
    canActivate: [SgiAuthGuard, DenyInvestigadorGuard]
  },
  {
    path: APP_ROUTE_NAMES.INV,
    loadChildren: () =>
      import('./module/inv/inv.module').then(
        (m) => m.InvModule
      ),
    canActivate: [SgiAuthGuard, AllowInvestigadorGuard]
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
