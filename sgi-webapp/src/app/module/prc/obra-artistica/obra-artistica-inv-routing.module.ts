import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ObraArtisticaListadoComponent } from './obra-artistica-listado/obra-artistica-listado.component';

const MSG_LISTADO_TITLE = marker('prc.obra-artistica.title');
const OBRA_ARTISTICA_KEY = marker('prc.obra-artistica.title');

const routes: SgiRoutes = [
  {
    path: '',
    component: ObraArtisticaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthority: 'PRC-VAL-INV-ER'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ObraArtisticaInvRoutingModule {
}
