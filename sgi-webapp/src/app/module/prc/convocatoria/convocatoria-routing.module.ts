import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ConvocatoriaBaremacionListadoComponent } from './convocatoria-baremacion-listado/convocatoria-baremacion-listado.component';

const MSG_LISTADO_TITLE = marker('prc.convocatoria.title');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaBaremacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-CON-V', 'PRC-CON-E', 'RC-CON-B', 'RC-CON-R']
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConvocatoriaRoutingModule {
}
