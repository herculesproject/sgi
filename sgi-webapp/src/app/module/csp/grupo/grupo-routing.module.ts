import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { GrupoListadoComponent } from './grupo-listado/grupo-listado.component';

const GRUPO_TITLE_KEY = marker('csp.grupo');

const routes: SgiRoutes = [
  {
    path: '',
    component: GrupoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: GRUPO_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-B', 'CSP-GIN-R', 'CSP-GIN-V']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GrupoRoutingModule { }
