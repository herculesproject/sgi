import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { ProyectoListadoComponent } from './proyecto-listado/proyecto-listado.component';

const MSG_LISTADO_TITLE = marker('csp.proyecto.listado.titulo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: ProyectoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoRoutingModule {
}
