import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { SgiAuthRoutes, SgiAuthGuard } from '@sgi/framework/auth';
import { ROUTE_NAMES } from '@core/route.names';


const routes: SgiAuthRoutes = [
  {
    path: '',
    component: ConvocatoriaReunionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAuthorityForAnyUO: 'ETI-CNV-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ConvocatoriaReunionCrearComponent,
    canActivate: [SgiAuthGuard],
    data: {
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
