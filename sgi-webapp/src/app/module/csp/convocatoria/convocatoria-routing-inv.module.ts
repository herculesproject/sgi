import { SgiRoutes } from '@core/route';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvocatoriaListadoInvComponent } from './convocatoria-listado-inv/convocatoria-listado-inv.component';

const MSG_CONVOCATORIAS_TITLE = marker('inv.convocatoria.listado.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_CONVOCATORIAS_TITLE
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaRoutingInvModule {
}
