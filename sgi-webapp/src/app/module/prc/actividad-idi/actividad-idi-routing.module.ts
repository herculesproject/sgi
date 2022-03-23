import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ActividadIdiListadoComponent } from './actividad-idi-listado/actividad-idi-listado.component';

const MSG_LISTADO_TITLE = marker('prc.actividad-idi');

const routes: SgiRoutes = [
  {
    path: '',
    component: ActividadIdiListadoComponent,
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
export class ActividadIdiRoutingModule {
}
