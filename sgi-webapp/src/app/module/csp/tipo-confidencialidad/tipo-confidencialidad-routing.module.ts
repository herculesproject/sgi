import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@herculesproject/framework/auth';
import { TipoConfidencialidadListadoComponent } from './tipo-confidencialidad-listado/tipo-confidencialidad-listado.component';

const MSG_LISTADO_TITLE = marker('csp.tipo-confidencialidad');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: TipoConfidencialidadListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TCONF-V', 'CSP-TCONF-C', 'CSP-TCONF-E', 'CSP-TCONF-B', 'CSP-TCONF-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TipoConfidencialidadRoutingModule {
}
