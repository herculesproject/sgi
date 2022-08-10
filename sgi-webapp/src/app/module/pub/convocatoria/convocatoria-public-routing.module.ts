import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ConvocatoriaPublicListadoComponent } from './convocatoria-public-listado/convocatoria-public-listado.component';

const CONVOCATORIA_KEY = marker('csp.convocatoria');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaPublicListadoComponent,
    data: {
      title: CONVOCATORIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConvocatoriaRoutingModule {
}
