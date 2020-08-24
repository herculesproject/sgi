import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('eti.convocatoriaReunion.listado.titulo');
const MSG_NEW_TITLE = marker('eti.convocatoriaReunion.crear.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaReunionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthorityForAnyUO: 'ETI-CNV-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    pathMatch: 'full',
    component: ConvocatoriaReunionCrearComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_NEW_TITLE,
      hasAuthorityForAnyUO: 'ETI-CNV-C'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaReunionRoutingModule {
}
