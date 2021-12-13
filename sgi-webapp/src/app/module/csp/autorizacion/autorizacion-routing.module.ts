import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { AutorizacionListadoComponent } from './autorizacion-listado/autorizacion-listado.component';


const AUTORIZACION_KEY = marker('csp.autorizacion');
const MSG_NEW_TITLE = marker('title.new.entity');
const routes: SgiRoutes = [
  {
    path: '',
    component: AutorizacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: AUTORIZACION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR'],
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AutorizacionRoutingModule { }
