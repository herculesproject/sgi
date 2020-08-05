import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UrlUtils } from '@core/utils/url-utils';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { SgiAuthRoutes, SgiAuthGuard } from '@sgi/framework/auth';


const routes: SgiAuthRoutes = [
  {
    path: UrlUtils.root.valueOf(),
    component: ConvocatoriaReunionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      hasAuthorityForAnyUO: 'ETI-CNV-V'
    }
  },
  {
    path: UrlUtils.crear.valueOf(),
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
