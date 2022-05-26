import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ComiteEditorialListadoComponent } from './comite-editorial-listado/comite-editorial-listado.component';

const MSG_LISTADO_TITLE = marker('prc.comite-editorial');
const COMITE_EDITORIAL_KEY = marker('prc.comite-editorial');

const routes: SgiRoutes = [
  {
    path: '',
    component: ComiteEditorialListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthority: 'PRC-VAL-INV-ER'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ComiteEditorialInvRoutingModule {
}
