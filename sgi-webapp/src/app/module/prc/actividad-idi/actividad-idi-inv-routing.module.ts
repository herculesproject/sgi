import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ActividadIdiListadoComponent } from './actividad-idi-listado/actividad-idi-listado.component';

const MSG_LISTADO_TITLE = marker('prc.actividad-idi');
const ACTIVIDAD_KEY = marker('prc.actividad-idi');

const routes: SgiRoutes = [
  {
    path: '',
    component: ActividadIdiListadoComponent,
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
export class ActividadIdiInvRoutingModule {
}
