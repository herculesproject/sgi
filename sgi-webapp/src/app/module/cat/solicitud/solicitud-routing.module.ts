import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SolicitudActualizarComponent } from './solicitud-actualizar/solicitud-actualizar.component';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('cat.solicitud.listado.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: SolicitudActualizarComponent,
    data: {
      title: MSG_LISTADO_TITLE
    }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudRoutingModule { }
