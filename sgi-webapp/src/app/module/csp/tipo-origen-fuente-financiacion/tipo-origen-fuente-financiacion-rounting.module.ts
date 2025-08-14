import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { TipoOrigenFuenteFinanciacionListadoComponent } from './tipo-origen-fuente-financiacion-listado/tipo-origen-fuente-financiacion-listado.component';

const MSG_LISTADO_TITLE = marker('menu.csp.tipo-origen-fuente-financiacion');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoOrigenFuenteFinanciacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TOFF-V', 'CSP-TOFF-C', 'CSP-TOFF-E', 'CSP-TOFF-B', 'CSP-TOFF-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoOrigenFuenteFinanciacionRoutingModule {
}
