import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ComiteEditorialListadoComponent } from './comite-editorial-listado/comite-editorial-listado.component';

const MSG_LISTADO_TITLE = marker('prc.comite-editorial');

const routes: SgiRoutes = [
  {
    path: '',
    component: ComiteEditorialListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ComiteEditorialRoutingModule {
}
