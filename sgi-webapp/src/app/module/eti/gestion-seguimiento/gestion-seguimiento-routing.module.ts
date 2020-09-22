import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { GestionSeguimientoListadoComponent } from './gestion-seguimiento-listado/gestion-seguimiento-listado.component';


const MSG_LISTADO_TITLE = marker('eti.gestionSeguimiento.listado.titulo');

const routes: SgiAuthRoutes = [
  {
    path: '',
    pathMatch: 'full',
    component: GestionSeguimientoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthorityForAnyUO: 'ETI-CNV-C'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GestionSeguimientoRoutingModule {
}
