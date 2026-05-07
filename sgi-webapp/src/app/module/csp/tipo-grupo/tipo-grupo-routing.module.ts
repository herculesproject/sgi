import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@herculesproject/framework/auth';
import { TipoGrupoListadoComponent } from './tipo-grupo-listado/tipo-grupo-listado.component';

const MSG_LISTADO_TITLE = marker('csp.tipo-grupo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: TipoGrupoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TGIN-V', 'CSP-TGIN-C', 'CSP-TGIN-E', 'CSP-TGIN-B', 'CSP-TGIN-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoGrupoRoutingModule {
}
