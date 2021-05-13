import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SolicitudListadoInvComponent } from './solicitud-listado-inv/solicitud-listado-inv.component';

const MSG_SOLICITUD_TITLE = marker('inv.solicitud.listado.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: SolicitudListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_SOLICITUD_TITLE,
      hasAuthorityForAnyUO: 'CSP-SOL-INV-V',
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudRoutingInvModule {
}
