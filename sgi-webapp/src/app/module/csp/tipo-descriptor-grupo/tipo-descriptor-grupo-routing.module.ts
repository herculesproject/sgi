import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@herculesproject/framework/auth';
import { TipoDescriptorGrupoListadoComponent } from './tipo-descriptor-grupo-listado/tipo-descriptor-grupo-listado.component';

const MSG_LISTADO_TITLE = marker('csp.tipo-descriptor-grupo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: TipoDescriptorGrupoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TDESG-V', 'CSP-TDESG-C', 'CSP-TDESG-E', 'CSP-TDESG-B', 'CSP-TDESG-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoDescriptorGrupoRoutingModule {
}
