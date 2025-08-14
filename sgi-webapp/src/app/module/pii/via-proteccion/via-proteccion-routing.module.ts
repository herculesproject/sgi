import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@herculesproject/framework/auth';
import { ViaProteccionListadoComponent } from './via-proteccion-listado/via-proteccion-listado.component';

const MSG_LISTADO_TITLE = marker('pii.via-proteccion');

const routes: SgiRoutes = [
  {
    path: '',
    component: ViaProteccionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-VPR-V', 'PII-VPR-C', 'PII-VPR-E', 'PII-VPR-B', 'PII-VPR-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ViaProteccionRoutingModule { }
